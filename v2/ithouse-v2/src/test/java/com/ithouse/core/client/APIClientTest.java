package com.ithouse.core.client;

import com.ithouse.core.client.config.ClientAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class APIClientTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ClientAutoConfiguration.class));

    @Test
    void apiClientBeanIsCreated() {
        contextRunner.withBean(WebClient.Builder.class, () -> mock(WebClient.Builder.class))
                .run(context -> {
                    assertThat(context).hasBean("apiClient");
                    assertThat(context).hasSingleBean(APIClient.class);
                });
    }
}
