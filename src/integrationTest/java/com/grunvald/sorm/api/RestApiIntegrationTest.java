package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты внутреннего REST API уведомлений.
 */
class RestApiIntegrationTest extends SormIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("PUT /notifications/push сохраняет уведомление в БД")
    void pushNotification_savedToDatabase() {
        var startAt = OffsetDateTime.now(ZoneOffset.UTC).toString();

        webTestClient.put().uri("/notifications/push")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "type": "MINORERROR",
                            "message": "integration test notification",
                            "startAt": "%s"
                        }
                        """.formatted(startAt))
                .exchange()
                .expectStatus().isOk();

        awaitRowCount("system_notifications", 1);
    }

    @Test
    @DisplayName("PUT /notifications/push с невалидным типом — 400")
    void pushNotification_invalidType_badRequest() {
        webTestClient.put().uri("/notifications/push")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "type": "NOT_A_TYPE",
                            "message": "msg",
                            "startAt": "%s"
                        }
                        """.formatted(OffsetDateTime.now(ZoneOffset.UTC)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("INVALID_PARAMETER");
    }

    @Test
    @DisplayName("PUT /notifications/push с пустым сообщением — 400")
    void pushNotification_blankMessage_badRequest() {
        webTestClient.put().uri("/notifications/push")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "type": "MINORERROR",
                            "message": "  ",
                            "startAt": "%s"
                        }
                        """.formatted(OffsetDateTime.now(ZoneOffset.UTC)))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("PUT /notifications/metric/alerts/push сохраняет алерт")
    void pushMetricAlert_savedToDatabase() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);

        webTestClient.put().uri("/notifications/metric/alerts/push")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "groupKey": "alert-group-1",
                            "status": "firing",
                            "receiver": "sorm",
                            "alerts": [
                                {
                                    "alertStatus": "firing",
                                    "startAt": "%s",
                                    "labels": [{"key": "job", "value": "sorm"}]
                                }
                            ]
                        }
                        """.formatted(now))
                .exchange()
                .expectStatus().isOk();

        awaitRowCount("system_notifications", 1);
    }

    @Test
    @DisplayName("несуществующий эндпоинт — 404 с ошибкой RESOURCE_NOT_FOUND")
    void unknownEndpoint_returns404() {
        webTestClient.get().uri("/no/such/endpoint")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("RESOURCE_NOT_FOUND");
    }

    @Test
    @DisplayName("метод не поддерживается — 405")
    void wrongMethod_returns405() {
        webTestClient.get().uri("/notifications/push")
                .exchange()
                .expectStatus().isEqualTo(405)
                .expectBody()
                .jsonPath("$.error").isEqualTo("METHOD_NOT_ALLOWED");
    }

    private void awaitRowCount(String table, int expected) {
        // событие публикуется асинхронно: ждём появления записи
        for (int attempt = 0; attempt < 50; attempt++) {
            try {
                if (countRows(table) >= expected) {
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        try {
            assertThat(countRows(table)).isEqualTo(expected);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
