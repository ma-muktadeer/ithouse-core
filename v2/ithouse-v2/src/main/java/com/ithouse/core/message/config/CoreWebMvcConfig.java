package com.ithouse.core.message.config;

import com.ithouse.core.message.resolver.FileEntityResolver;
import com.ithouse.core.message.resolver.MessageHeaderResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CoreWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FileEntityResolver fileEntityResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MessageHeaderResolver(fileEntityResolver));
        resolvers.add(fileEntityResolver);
    }
}
