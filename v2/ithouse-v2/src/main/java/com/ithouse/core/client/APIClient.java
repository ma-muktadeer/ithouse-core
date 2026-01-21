package com.ithouse.core.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Functional interface for making API requests.
 * Hides the underlying implementation (WebClient).
 */
@FunctionalInterface
public interface APIClient {

    /**
     * Executes an HTTP request.
     *
     * @param method          HTTP method (GET, POST, etc.)
     * @param url             Target URL
     * @param body            Request body (optional)
     * @param headersConsumer Consumer to configure HTTP headers (optional)
     * @param params          Query parameters (optional)
     * @return Mono containing the response body as a String
     */
    Mono<String> executeRequest(HttpMethod method, String url, Object body, Consumer<HttpHeaders> headersConsumer,
            Map<String, Object> params);

}
