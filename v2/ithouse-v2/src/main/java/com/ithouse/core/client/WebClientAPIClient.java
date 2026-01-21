package com.ithouse.core.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Implementation of APIClient using Spring WebClient.
 * This class is package-private to hide it from library consumers.
 */
class WebClientAPIClient implements APIClient {

    private final WebClient webClient;

    public WebClientAPIClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<String> executeRequest(HttpMethod method, String url, Object body,
            Consumer<HttpHeaders> headersConsumer, Map<String, Object> params) {
        URI uri = buildUri(url, params);

        WebClient.RequestBodySpec requestBodySpec = webClient.method(method).uri(uri);

        if (headersConsumer != null) {
            requestBodySpec.headers(headersConsumer);
        }

        if (body != null && method == HttpMethod.POST) {
            requestBodySpec.bodyValue(body);
        }

        return requestBodySpec.retrieve().bodyToMono(String.class);
    }

    private URI buildUri(String url, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return URI.create(url);
        }
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        params.forEach((key, value) -> {
            if (value != null) {
                if (value instanceof Collection) {
                    ((Collection<?>) value).forEach(item -> {
                        builder.queryParam(key, item);
                    });
                } else {
                    builder.queryParam(key, value);
                }
            }
        });
        return builder.build().toUri();
    }
}
