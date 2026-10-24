package com.grunvald.sorm.presentation.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiKeyFilterConfigTest {

    private ApiKeyFilterConfig config;
    private WebFilterChain chain;

    @BeforeEach
    void setUp() {
        config = new ApiKeyFilterConfig();
        config.setApiKey("test-api-key-123456");
        config.setExcludeEndpoints(List.of("/actuator/health"));
        chain = mock(WebFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void validKey_requestPasses() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.put("/query").header("X-API-Key", "test-api-key-123456").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        verify(chain, times(1)).filter(any());
        assertEquals(null, exchange.getResponse().getStatusCode(), "статус не должен переопределяться фильтром");
    }

    @Test
    void missingOrWrongKey_returns401() {
        var exchange = MockServerWebExchange.from(
                MockServerHttpRequest.put("/query").header("X-API-Key", "wrong-key").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void noKeyHeader_returns401() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.put("/query").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void excludedEndpoint_passesWithoutKey() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/actuator/health").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        verify(chain, times(1)).filter(any());
    }

    @Test
    void excludedEndpoint_otherPath_stillRequiresKey() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/actuator/metrics").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
    }

    @Test
    void emptyApiKeyConfigured_requestPasses() {
        // защита от пустого ключа в конфиге: фильтр пропускает всё
        config.setApiKey("");
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.put("/query").build());

        config.apiKeyFilter().filter(exchange, chain).block();

        verify(chain, times(1)).filter(any());
    }
}
