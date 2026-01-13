package com.ithouse.core.message.resolver;

import com.ithouse.core.message.AbstractMessageHeader;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MessageHeaderResolver implements HandlerMethodArgumentResolver {
    private static final Logger logger = LogManager.getLogger(MessageHeaderResolver.class);

    private static final String VIA = "Via";
    public final FileEntityResolver fileEntityResolver;
    public MessageHeaderResolver(FileEntityResolver fileEntityResolver) {
        this.fileEntityResolver = fileEntityResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AbstractMessageHeader.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        AbstractMessageHeader header = new AbstractMessageHeader();

        for (Field field : AbstractMessageHeader.class.getDeclaredFields()) {
            String value = webRequest.getParameter(field.getName());
            if (value != null) {
                field.setAccessible(true);
                try {
                    if (field.getType() == String.class) {
                        field.set(header, value);
                    } else if (field.getType() == Integer.class) {
                        field.set(header, Integer.valueOf(value));
                    } else if (field.getType() == Map.class) {
                        field.set(header, fileEntityResolver.covert2Map(value));
                    }
                } catch (Exception e) {
                    logger.error("Error setting field value: {}", field.getName(), e);
                }
            }
        }

        // Set IP addresses
        header.setSenderSourceIPAddress(request.getRemoteAddr());
        header.setSenderGatewayIPAddress(request.getHeader(VIA));

        return header;
    }
}
