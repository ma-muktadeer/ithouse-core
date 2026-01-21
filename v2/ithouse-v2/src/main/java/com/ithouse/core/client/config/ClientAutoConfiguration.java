package com.ithouse.core.client.config;

import com.ithouse.core.client.APIClient;
import com.ithouse.core.client.APIClientFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration")
public class ClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public APIClient apiClient(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        WebClient.Builder builder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
        return APIClientFactory.createWebClientAPIClient(builder);
    }
}
