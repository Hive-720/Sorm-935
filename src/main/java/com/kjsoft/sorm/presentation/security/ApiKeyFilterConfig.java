package com.kjsoft.sorm.presentation.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.WebFilter;

import java.util.List;


@Getter
@Setter
@Validated
@Configuration
@ConditionalOnProperty(name = "sorm.security.enabled", havingValue = "true")
@ConfigurationProperties(prefix = "sorm.security")
public class ApiKeyFilterConfig {
    @NotBlank
    @Size(min = 16, message = "The API key must be at least 16 characters long")
    private String apiKey;
    private List<String> excludeEndpoints;

    @Bean
    public WebFilter apiKeyFilter() {
        return (exchange, chain) -> {
            if (apiKey == null || apiKey.isEmpty()) {
                return chain.filter(exchange);
            }

            var request = exchange.getRequest();

            if (isExcludeEndpoint(request)) {
                return chain.filter(exchange);
            }

            var apiKey = request.getHeaders().getFirst("X-API-Key");
            if (!this.apiKey.equals(apiKey)) {
                //TODO нужно сделать уведомление!!! А так же сформировать правильный ответ
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean isExcludeEndpoint(ServerHttpRequest request) {
        if (excludeEndpoints == null || excludeEndpoints.isEmpty()) {
            return false;
        }

        var path = request.getPath().value();
        return excludeEndpoints.stream()
                .anyMatch(endpoint -> endpoint.equals(path));
    }
}