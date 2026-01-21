package com.ithouse.core.client;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory to create APIClient instances.
 * This is used by the configuration to instantiate the hidden implementation.
 */
public final class APIClientFactory {

    private APIClientFactory() {
        // static factory
    }

    public static APIClient createWebClientAPIClient(WebClient.Builder webClientBuilder) {
        return new WebClientAPIClient(webClientBuilder);
    }
}
