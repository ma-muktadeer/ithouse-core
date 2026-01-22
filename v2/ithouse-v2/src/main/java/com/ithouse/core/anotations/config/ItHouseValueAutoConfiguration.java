package com.ithouse.core.anotations.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ithouse.core.anotations.injectors.ItHouseDBValueInjector;
import com.ithouse.core.anotations.provider.ItHouseConfigProvider;
import com.ithouse.core.anotations.services.ConfigRefreshService;
import com.ithouse.core.anotations.services.ConfigRefreshServiceFactory;
import com.ithouse.core.anotations.services.ItHouseDBValueService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@AutoConfiguration
public class ItHouseValueAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ItHouseDBValueService itHouseDBValueService(Optional<ItHouseConfigProvider> configProvider) {
        return new ItHouseDBValueService(configProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ItHouseDBValueInjector itHouseDBValueInjector(ItHouseDBValueService itHouseDBValueService,
            ObjectMapper objectMapper) {
        return new ItHouseDBValueInjector(itHouseDBValueService, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigRefreshService configRefreshService(
            ObjectProvider<ItHouseDBValueInjector> itHouseDBValueInjectorProvider) {
        return ConfigRefreshServiceFactory.create(itHouseDBValueInjectorProvider);
    }
}
