package com.ithouse.core.message.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ithouse.core.message.interfaces.EnableFile;
import com.ithouse.core.message.processor.services.ProcessorService;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

@Component
public class FileEntityResolver implements HandlerMethodArgumentResolver {

    private final ProcessorService processorService;

    public FileEntityResolver(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return EnableFile.class.equals(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            @Nullable WebDataBinderFactory binderFactory) throws Exception {

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) webRequest.getNativeRequest();
        List<MultipartFile> files = multipartRequest.getFiles("files");

        String type = webRequest.getParameter("type");
        String metadata = webRequest.getParameter("payload");

        if (type == null) {
            throw new IllegalArgumentException("Missing type parameter");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Missing metadata parameter");
        }

        return processorService.buildFileEntity(type, metadata, files);
    }

    public Map<String, String> covert2Map(String value) throws JsonProcessingException {
        return processorService.covertString2Map(value);
    }
}
