package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Интеграционная проверка метрик: WebFlux HTTP-observations
 * (http.server.requests), GraphQL-observations (graphql.request)
 * и гейджи пула R2DBC (r2dbc.pool.*) пишутся в MeterRegistry.
 */
class MetricsIntegrationTest extends SormIntegrationTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Test
    @DisplayName("GraphQL-запрос записывает HTTP- и GraphQL-метрики")
    void graphqlRequest_recordsHttpAndGraphQlMetrics() {
        webTestClient.post().uri("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", "query { request { requestID offline maxPageSize } }"))
                .exchange()
                .expectStatus().isOk();

        // observation останавливается в doFinally уже после завершения обмена
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(meterRegistry.find("http.server.requests").timer()).isNotNull();
            assertThat(meterRegistry.find("graphql.request").timer()).isNotNull();
        });
    }

    @Test
    @DisplayName("WebClient из авто-конфигнутого builder записывает http.client.requests")
    void webClientFromAutoConfiguredBuilder_recordsClientHttpMetrics() {
        // whmcsWebClient собирается из этого же builder, поэтому проверяем
        // прогон запроса через него: client-observation навешивается автоконфигурацией
        var server = HttpServer.create().port(0)
                .handle((request, response) -> response.sendString(Mono.just("ok")));
        var disposableServer = server.bindNow();
        try {
            var body = webClientBuilder
                    .baseUrl("http://localhost:%d".formatted(disposableServer.port()))
                    .build()
                    .get().uri("/test")
                    .retrieve().bodyToMono(String.class)
                    .block(Duration.ofSeconds(10));

            assertThat(body).isEqualTo("ok");
            await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                    assertThat(meterRegistry.find("http.client.requests").timer()).isNotNull());
        } finally {
            disposableServer.disposeNow();
        }
    }

    @Test
    @DisplayName("GraphQL-запрос в БД записывает r2dbc.query и гистограмму http.server.requests")
    void paymentMethodsQuery_recordsR2dbcQueryAndHistogramBuckets() {
        webTestClient.post().uri("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", """
                        query { request { paymentMethods(filter: {}, offset: 0, limit: 25) {
                            cursor hasNextPage result { id moduleName displayName deleted }
                        } } }
                        """))
                .exchange()
                .expectStatus().isOk();

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(meterRegistry.find("r2dbc.query").timer()).isNotNull());

        var timer = meterRegistry.get("http.server.requests").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.takeSnapshot().histogramCounts().length).isGreaterThan(1);
    }

    @Test
    @DisplayName("Гейджи пула R2DBC зарегистрированы")
    void r2dbcPoolGauges_bound() {
        assertThat(meterRegistry.find("r2dbc.pool.allocated").gauge()).isNotNull();
        assertThat(meterRegistry.find("r2dbc.pool.acquired").gauge()).isNotNull();
    }
}
