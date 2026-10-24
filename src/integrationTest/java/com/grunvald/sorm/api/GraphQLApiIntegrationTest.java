package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты GraphQL API: приложение поднимается целиком
 * (Liquibase, R2DBC, GraphQL), запросы уходят по HTTP на /query.
 */
class GraphQLApiIntegrationTest extends SormIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private WebTestClient graphql;

    @BeforeEach
    void setUpClient() {
        graphql = webTestClient.mutate()
                .responseTimeout(java.time.Duration.ofSeconds(30))
                .build();
    }

    private WebTestClient.ResponseSpec executeQuery(String query) {
        return executeQuery(query, java.util.Map.of());
    }

    private WebTestClient.ResponseSpec executeQuery(String query, java.util.Map<String, Object> variables) {
        return graphql.post().uri("/query")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(java.util.Map.of("query", query, "variables", variables))
                .exchange();
    }

    @Test
    @DisplayName("request возвращает requestID, offline=false и maxPageSize")
    void request_returnsServiceInfo() {
        executeQuery("query { request { requestID offline maxPageSize } }")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.request.requestID").isNotEmpty()
                .jsonPath("$.data.request.offline").isEqualTo(false)
                .jsonPath("$.data.request.maxPageSize").isEqualTo(100);
    }

    @Test
    @DisplayName("getSchema возвращает непустую SDL-схему")
    void getSchema_returnsSchemaDefinition() {
        executeQuery("query { getSchema }")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.getSchema").value(schema ->
                        assertThat((String) schema).contains("type PaymentMethod"));
    }

    @Test
    @DisplayName("paymentMethods: пустая база — пустой результат без курсора")
    void paymentMethods_emptyDatabase() {
        executeQuery("""
                query { request { paymentMethods(filter: {}, offset: 0, limit: 25) {
                    cursor hasNextPage result { id moduleName displayName deleted }
                } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.request.paymentMethods.cursor").doesNotExist()
                .jsonPath("$.data.request.paymentMethods.hasNextPage").isEqualTo(false)
                .jsonPath("$.data.request.paymentMethods.result").isEmpty();
    }

    @Test
    @DisplayName("paymentMethods: пагинация по курсору")
    void paymentMethods_cursorPagination() throws SQLException {
        insertPaymentMethod(1, "mailin", "Mailin", "2026-01-01 10:00:00+00", false);
        insertPaymentMethod(2, "stripe", "Stripe", "2026-01-02 10:00:00+00", false);
        insertPaymentMethod(3, "paypal", "PayPal", "2026-01-03 10:00:00+00", false);

        // страница 1: лимит 2, строки отсортированы по id
        var page1 = executeQuery("""
                query { request { paymentMethods(filter: {}, offset: 0, limit: 2) {
                    cursor hasNextPage result { id moduleName }
                } } }
                """)
                .expectStatus().isOk()
                .expectBody().jsonPath("$.data.request.paymentMethods.hasNextPage").isEqualTo(true)
                .jsonPath("$.data.request.paymentMethods.result.length()").isEqualTo(2)
                .jsonPath("$.data.request.paymentMethods.result[0].moduleName").isEqualTo("mailin")
                .jsonPath("$.data.request.paymentMethods.result[1].moduleName").isEqualTo("stripe")
                .returnResult();

        var cursor = readCursor(page1);

        // страница 2: по курсору
        executeQuery("""
                query($cursor: String) { request { paymentMethods(filter: {}, cursor: $cursor, limit: 2) {
                    cursor hasNextPage result { id moduleName }
                } } }
                """, java.util.Map.of("cursor", cursor))
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.request.paymentMethods.hasNextPage").isEqualTo(false)
                .jsonPath("$.data.request.paymentMethods.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.paymentMethods.result[0].moduleName").isEqualTo("paypal");
    }

    @Test
    @DisplayName("paymentMethods: фильтр по updatedAt (ge) возвращает только новые записи")
    void paymentMethods_updatedAtFilter() throws SQLException {
        insertPaymentMethod(1, "old", "Old", "2026-01-01 10:00:00+00", false);
        insertPaymentMethod(2, "new", "New", "2026-06-01 10:00:00+00", false);

        executeQuery("""
                query { request { paymentMethods(
                    filter: { updatedAt: { utc: { ge: "2026-03-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) {
                    hasNextPage result { moduleName }
                } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.request.paymentMethods.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.paymentMethods.result[0].moduleName").isEqualTo("new");
    }

    @Test
    @DisplayName("limit больше maxPageSize отклоняется валидацией")
    void paymentMethods_limitAboveMax_rejected() {
        executeQuery("""
                query { request { paymentMethods(filter: {}, offset: 0, limit: 101) { hasNextPage } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty();
    }

    @Test
    @DisplayName("неизвестное поле в запросе — ошибка валидации схемы")
    void paymentMethods_unknownField_error() {
        executeQuery("query { request { paymentMethods(filter: {}) { nonexistentField } } }")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty();
    }

    private String readCursor(org.springframework.test.web.reactive.server.EntityExchangeResult<byte[]> result) {
        var body = new String(result.getResponseBody());
        var marker = "\"cursor\":\"";
        var start = body.indexOf(marker) + marker.length();
        return body.substring(start, body.indexOf('"', start));
    }
}
