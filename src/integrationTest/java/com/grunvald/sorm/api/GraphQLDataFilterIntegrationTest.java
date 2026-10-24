package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.sql.SQLException;
import java.util.Map;

/**
 * Интеграционные тесты фильтров GraphQL API: сквозная проверка
 * datafilter (QueryDslSqlGenerator + репозитории + джойны) через HTTP.
 */
class GraphQLDataFilterIntegrationTest extends SormIntegrationTest {

    private static final String D1 = "2026-01-01T00:00:00+00:00";
    private static final String D2 = "2026-01-02T00:00:00+00:00";
    private static final String D3 = "2026-01-03T00:00:00+00:00";

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
        return executeQuery(query, Map.of());
    }

    private WebTestClient.ResponseSpec executeQuery(String query, Map<String, Object> variables) {
        return graphql.post().uri("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", query, "variables", variables))
                .exchange();
    }

    private void insertClient(long id, String email, String firstName, String lastName,
                              String fullName, String city, String taxId) throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.clients
                        (id, owner_user_id, email, first_name, last_name, full_name, city, tax_id, date_created)
                        VALUES (?, 1, ?, ?, ?, ?, ?, ?, ?::timestamptz)
                        """,
                id, email, firstName, lastName, fullName, city, taxId, "2026-01-01T10:00:00+00:00");
    }

    private void insertOrder(long id, long clientId, String ipAddress, String date, Long invoiceId) throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.orders
                        (id, client_id, ip_address, date, invoice_id, amount, status)
                        VALUES (?, ?, ?, ?::timestamptz, ?, 100.00, 'Pending')
                        """,
                id, clientId, ipAddress, date, invoiceId);
    }

    // ===================== orders: джойны и условия =====================

    @Test
    @DisplayName("orders: фильтр по client.email — INNER JOIN на clients")
    void orders_byClientEmail_join() throws SQLException {
        insertClient(1, "buyer@example.com", "Ivan", "Petrov", "Иван Петров", "Moscow", null);
        insertClient(2, "other@example.com", "Petr", "Sidorov", "Пётр Сидоров", "Tula", null);
        insertOrder(1, 1, "10.0.0.1", D1, null);
        insertOrder(2, 2, "10.0.0.2", D2, null);

        executeQuery("""
                query { request { orders(filter: { client: { email: { email: { eq: "buyer@example.com" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id client { id } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1")
                .jsonPath("$.data.request.orders.result[0].client.id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: фильтр по client.name.lastName — джойн + строковый eq")
    void orders_byClientLastName_join() throws SQLException {
        insertClient(1, "buyer@example.com", "Ivan", "Petrov", "Иван Петров", "Moscow", null);
        insertClient(2, "other@example.com", "Petr", "Sidorov", "Пётр Сидоров", "Tula", null);
        insertOrder(1, 1, "10.0.0.1", D1, null);
        insertOrder(2, 2, "10.0.0.2", D2, null);

        executeQuery("""
                query { request { orders(filter: { client: { name: { lastName: { eq: "Petrov" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: фильтр по items.domain — INNER JOIN на order_items")
    void orders_byItemsDomain_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertOrder(1, 1, "10.0.0.1", D1, null);
        insertOrder(2, 1, "10.0.0.2", D2, null);
        executeSql("INSERT INTO sorm_schema.order_items (id, order_id, domain) VALUES (1, 1, 'shop.example.ru')");
        executeSql("INSERT INTO sorm_schema.order_items (id, order_id, domain) VALUES (2, 2, 'other.example.ru')");

        executeQuery("""
                query { request { orders(filter: { items: { domain: { domainName: { eq: "shop.example.ru" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: фильтр по invoice.dueDate — INNER JOIN на invoices")
    void orders_byInvoiceDueDate_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.invoices (id, client_id, due_date, date, items)
                        VALUES (10, 1, ?::timestamptz, ?::timestamptz, '[]'::jsonb)
                        """, "2026-03-01T00:00:00+00:00", D1);
        executeSql("""
                        INSERT INTO sorm_schema.invoices (id, client_id, due_date, date, items)
                        VALUES (11, 1, ?::timestamptz, ?::timestamptz, '[]'::jsonb)
                        """, "2026-09-01T00:00:00+00:00", D2);
        insertOrder(1, 1, "10.0.0.1", D1, 10L);
        insertOrder(2, 1, "10.0.0.2", D2, 11L);

        executeQuery("""
                query { request { orders(filter: { invoice: { dueDate: { utc: { lt: "2026-06-01T00:00:00+00:00" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: date between — включая границы")
    void orders_byDateBetween() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertOrder(1, 1, "10.0.0.1", "2026-01-10T10:00:00+00:00", null);
        insertOrder(2, 1, "10.0.0.2", "2026-01-20T10:00:00+00:00", null);
        insertOrder(3, 1, "10.0.0.3", "2026-01-30T10:00:00+00:00", null);

        executeQuery("""
                query { request { orders(filter: { date: { utc: { between:
                    { min: "2026-01-15T00:00:00+00:00", max: "2026-01-25T00:00:00+00:00" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("2");
    }

    @Test
    @DisplayName("orders: not-фильтр по ipAddress")
    void orders_notIpAddress() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertOrder(1, 1, "10.0.0.1", D1, null);
        insertOrder(2, 1, "10.0.0.2", D2, null);

        executeQuery("""
                query { request { orders(filter: { not: { ipAddress: { ip: { eq: "10.0.0.2" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: пустая база — пустой результат")
    void orders_emptyDatabase() {
        executeQuery("""
                query { request { orders(filter: {}, offset: 0, limit: 25) { cursor hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.cursor").doesNotExist()
                .jsonPath("$.data.request.orders.hasNextPage").isEqualTo(false)
                .jsonPath("$.data.request.orders.result").isEmpty();
    }

    @Test
    @DisplayName("orders: пагинация по курсору")
    void orders_cursorPagination() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertOrder(1, 1, "10.0.0.1", D1, null);
        insertOrder(2, 1, "10.0.0.2", D2, null);
        insertOrder(3, 1, "10.0.0.3", D3, null);

        var page1 = executeQuery("""
                query { request { orders(filter: {}, offset: 0, limit: 2) { cursor hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.hasNextPage").isEqualTo(true)
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(2)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("1")
                .jsonPath("$.data.request.orders.result[1].id").isEqualTo("2")
                .returnResult();

        var cursor = readCursor(page1);

        executeQuery("""
                query($cursor: String) { request { orders(filter: {}, cursor: $cursor, limit: 2)
                    { hasNextPage result { id } } } }
                """, Map.of("cursor", cursor))
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.hasNextPage").isEqualTo(false)
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.orders.result[0].id").isEqualTo("3");
    }

    // ===================== activityLog: джойны и условия =====================

    @Test
    @DisplayName("activityLog: фильтр по username.login")
    void activityLog_byUsername() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (1, 1, 'admin@example.com', '192.168.0.1', ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (2, 1, 'user@example.com', '192.168.0.2', ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { activityLog(filter: { username: { login: { eq: "admin@example.com" } } },
                    offset: 0, limit: 25) { hasNextPage result { id username { login } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.activityLog.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.activityLog.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("activityLog: фильтр по client.email — INNER JOIN на clients")
    void activityLog_byClientEmail_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (1, 1, 'admin@example.com', '192.168.0.1', ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (2, 2, 'admin@example.com', '192.168.0.2', ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { activityLog(filter: { client: { email: { email: { eq: "buyer@example.com" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.activityLog.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.activityLog.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("activityLog: комбинация ipAddress и date (AND условий)")
    void activityLog_ipAndDate() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (1, 1, 'admin@example.com', '192.168.0.1', ?::timestamptz)
                        """, "2026-01-10T10:00:00+00:00");
        executeSql("""
                        INSERT INTO sorm_schema.activity_logs (id, client_id, username, ip_address, date)
                        VALUES (2, 1, 'admin@example.com', '192.168.0.2', ?::timestamptz)
                        """, "2026-01-20T10:00:00+00:00");

        executeQuery("""
                query { request { activityLog(filter: { ipAddress: { ip: { eq: "192.168.0.1" } },
                    date: { utc: { lt: "2026-01-15T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.activityLog.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.activityLog.result[0].id").isEqualTo("1");
    }

    // ===================== clients: условия =====================

    @Test
    @DisplayName("clients: фильтр по inn (tax_id)")
    void clients_byInn() throws SQLException {
        insertClient(1, "a@example.com", null, null, null, null, "7701111111");
        insertClient(2, "b@example.com", null, null, null, null, "7702222222");

        executeQuery("""
                query { request { clients(filter: { inn: { inn: { eq: "7701111111" } } },
                    offset: 0, limit: 25) { hasNextPage result { id inn { inn } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clients: фильтр по email regexp")
    void clients_byEmailRegexp() throws SQLException {
        insertClient(1, "ivan@example.com", null, null, null, null, null);
        insertClient(2, "petr@example.com", null, null, null, null, null);

        executeQuery("""
                query { request { clients(filter: { email: { email: { regexp: "^ivan@" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clients: фильтр по имени (firstName eq)")
    void clients_byFirstName() throws SQLException {
        insertClient(1, "a@example.com", "Ivan", "Petrov", "Иван Петров", null, null);
        insertClient(2, "b@example.com", "Petr", "Sidorov", "Пётр Сидоров", null, null);

        executeQuery("""
                query { request { clients(filter: { name: { name: { eq: "Ivan" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clients: полнотекстовый поиск anyofterms по full_name")
    void clients_byFullName_anyofterms() throws SQLException {
        insertClient(1, "a@example.com", "Ivan", "Petrov", "Иван Петров", null, null);
        insertClient(2, "b@example.com", "Petr", "Sidorov", "Пётр Сидоров", null, null);

        executeQuery("""
                query { request { clients(filter: { name: { fullName: { anyofterms: "Петров" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clients: фильтр по городу")
    void clients_byCity() throws SQLException {
        insertClient(1, "a@example.com", null, null, null, "Moscow", null);
        insertClient(2, "b@example.com", null, null, null, "Tula", null);

        executeQuery("""
                query { request { clients(filter: { address: { city: { eq: "Moscow" } } },
                    offset: 0, limit: 25) { hasNextPage result { id address { city } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clients: email in [два значения]")
    void clients_byEmailIn() throws SQLException {
        insertClient(1, "a@example.com", null, null, null, null, null);
        insertClient(2, "b@example.com", null, null, null, null, null);
        insertClient(3, "c@example.com", null, null, null, null, null);

        executeQuery("""
                query { request { clients(filter: { email: { email: { in: ["a@example.com", "b@example.com"] } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(2)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1")
                .jsonPath("$.data.request.clients.result[1].id").isEqualTo("2");
    }

    @Test
    @DisplayName("clients: or-фильтр объединяет два условия")
    void clients_orFilter() throws SQLException {
        insertClient(1, "a@example.com", "Ivan", "Petrov", null, null, null);
        insertClient(2, "petr@example.com", "Petr", "Sidorov", null, null, null);
        insertClient(3, "c@example.com", "Anna", "Ivanova", null, null, null);

        executeQuery("""
                query { request { clients(filter: { or: [
                        { name: { name: { eq: "Ivan" } } },
                        { email: { email: { eq: "petr@example.com" } } }
                    ] }, offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clients.result.length()").isEqualTo(2)
                .jsonPath("$.data.request.clients.result[0].id").isEqualTo("1")
                .jsonPath("$.data.request.clients.result[1].id").isEqualTo("2");
    }

    // ===================== emails: массивы и джойны =====================

    @Test
    @DisplayName("emails: фильтр по адресату")
    void emails_byTo() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (1, 1, 'dest@example.com', '{}'::varchar[], ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (2, 1, 'other@example.com', '{}'::varchar[], ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { emails(filter: { to: { email: { eq: "dest@example.com" } } },
                    offset: 0, limit: 25) { hasNextPage result { id to { email } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.emails.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.emails.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("emails: фильтр по cc — поиск в массиве")
    void emails_byCc_any() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (1, 1, 'dest@example.com', '{"cc@example.ru"}'::varchar[], ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (2, 1, 'dest@example.com', '{"other@example.ru"}'::varchar[], ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { emails(filter: { cc: { email: { eq: "cc@example.ru" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.emails.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.emails.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("emails: фильтр по client — INNER JOIN на clients")
    void emails_byClient_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (1, 1, 'dest@example.com', '{}'::varchar[], ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.emails (id, client_id, "to", cc, date)
                        VALUES (2, 2, 'dest@example.com', '{}'::varchar[], ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { emails(filter: { client: { email: { email: { eq: "buyer@example.com" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.emails.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.emails.result[0].id").isEqualTo("1");
    }

    // ===================== прочие сущности =====================

    @Test
    @DisplayName("contacts: фильтр по клиенту (джойн) и updatedAt")
    void contacts_byClientAndUpdatedAt() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.contacts (id, client_id, first_name, updated_at)
                        VALUES (1, 1, 'Support', ?::timestamptz)
                        """, "2026-06-01T00:00:00+00:00");
        executeSql("""
                        INSERT INTO sorm_schema.contacts (id, client_id, first_name, updated_at)
                        VALUES (2, 2, 'Support', ?::timestamptz)
                        """, "2026-01-01T00:00:00+00:00");

        executeQuery("""
                query { request { contacts(filter: { client: { email: { email: { eq: "buyer@example.com" } } },
                    updatedAt: { utc: { ge: "2026-03-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.contacts.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.contacts.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("credits: фильтр по клиенту (джойн)")
    void credits_byClient_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.credits (id, client_id, amount, date)
                        VALUES (1, 1, 100.00, ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.credits (id, client_id, amount, date)
                        VALUES (2, 2, 200.00, ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { credits(filter: { client: { email: { email: { eq: "buyer@example.com" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id amount } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.credits.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.credits.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("transactions: фильтр по клиенту (джойн) и дате")
    void transactions_byClientAndDate() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.transactions (id, client_id, amount_in, date)
                        VALUES (1, 1, 100.00, ?::timestamptz)
                        """, "2026-06-01T00:00:00+00:00");
        executeSql("""
                        INSERT INTO sorm_schema.transactions (id, client_id, amount_in, date)
                        VALUES (2, 2, 200.00, ?::timestamptz)
                        """, "2026-06-01T00:00:00+00:00");

        executeQuery("""
                query { request { transactions(filter: { client: { email: { email: { eq: "buyer@example.com" } } },
                    date: { utc: { ge: "2026-01-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.transactions.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.transactions.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clientLogins: фильтр по ipAddress")
    void clientLogins_byIpAddress() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.client_logins (id, client_id, ip_address, date)
                        VALUES (1, 1, '203.0.113.10', ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.client_logins (id, client_id, ip_address, date)
                        VALUES (2, 1, '203.0.113.11', ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { clientLogins(filter: { ipAddress: { ip: { eq: "203.0.113.10" } } },
                    offset: 0, limit: 25) { hasNextPage result { id ipAddress { ip } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clientLogins.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clientLogins.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clientGroups: фильтр по updatedAt")
    void clientGroups_byUpdatedAt() throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.client_groups (id, name, updated_at)
                        VALUES (1, 'VIP', ?::timestamptz)
                        """, "2026-06-01T00:00:00+00:00");
        executeSql("""
                        INSERT INTO sorm_schema.client_groups (id, name, updated_at)
                        VALUES (2, 'Regular', ?::timestamptz)
                        """, "2026-01-01T00:00:00+00:00");

        executeQuery("""
                query { request { clientGroups(filter: { updatedAt: { utc: { ge: "2026-03-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id name } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clientGroups.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clientGroups.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("users: фильтр по email + клиенты через batchloader")
    void users_byEmail_withClients() throws SQLException {
        executeSql("INSERT INTO sorm_schema.users (id, email, full_name) VALUES (1, 'owner@example.com', 'Owner')");
        executeSql("INSERT INTO sorm_schema.users (id, email, full_name) VALUES (2, 'lonely@example.com', 'Lonely')");
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("INSERT INTO sorm_schema.client_user_relations (client_id, user_id) VALUES (1, 1)");
        executeSql("INSERT INTO sorm_schema.client_user_relations (client_id, user_id) VALUES (2, 1)");

        executeQuery("""
                query { request { users(filter: { email: { email: { eq: "owner@example.com" } } },
                    offset: 0, limit: 25) { hasNextPage result { id email { email } clients { id } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.users.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.users.result[0].id").isEqualTo("1")
                .jsonPath("$.data.request.users.result[0].clients.length()").isEqualTo(2);

        // пользователь без связей: dataloader не возвращает ключ — поле clients становится null,
        // а null-поля в GraphQL JSON опускаются
        executeQuery("""
                query { request { users(filter: { email: { email: { eq: "lonely@example.com" } } },
                    offset: 0, limit: 25) { hasNextPage result { id clients { id } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.users.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.users.result[0].clients").doesNotExist();
    }

    @Test
    @DisplayName("products: фильтр по updatedAt")
    void products_byUpdatedAt() throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.products (id, type, name, prices, updated_at, deleted)
                        VALUES (1, 'hosting', 'Hosting XL', '[]'::jsonb, ?::timestamptz, false)
                        """, "2026-06-01T00:00:00+00:00");
        executeSql("""
                        INSERT INTO sorm_schema.products (id, type, name, prices, updated_at, deleted)
                        VALUES (2, 'domain', 'Domain', '[]'::jsonb, ?::timestamptz, false)
                        """, "2026-01-01T00:00:00+00:00");

        executeQuery("""
                query { request { products(filter: { updatedAt: { utc: { ge: "2026-03-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id name } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.products.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.products.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("clientProducts: фильтр по домену")
    void clientProducts_byDomain() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.client_products (id, client_id, domain, configs)
                        VALUES (1, 1, 'shop.example.ru', '[]'::jsonb)
                        """);
        executeSql("""
                        INSERT INTO sorm_schema.client_products (id, client_id, domain, configs)
                        VALUES (2, 1, 'other.example.ru', '[]'::jsonb)
                        """);

        executeQuery("""
                query { request { clientProducts(filter: { domain: { domainName: { eq: "shop.example.ru" } } },
                    offset: 0, limit: 25) { hasNextPage result { id domain { domainName } } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.clientProducts.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.clientProducts.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("affiliates: фильтр по клиенту (джойн)")
    void affiliates_byClient_join() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        insertClient(2, "other@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.affiliates (id, client_id, date)
                        VALUES (1, 1, ?::timestamptz)
                        """, D1);
        executeSql("""
                        INSERT INTO sorm_schema.affiliates (id, client_id, date)
                        VALUES (2, 2, ?::timestamptz)
                        """, D2);

        executeQuery("""
                query { request { affiliates(filter: { client: { email: { email: { eq: "buyer@example.com" } } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.affiliates.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.affiliates.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("invoices: фильтр по dueDate")
    void invoices_byDueDate() throws SQLException {
        insertClient(1, "buyer@example.com", null, null, null, null, null);
        executeSql("""
                        INSERT INTO sorm_schema.invoices (id, client_id, due_date, date, items)
                        VALUES (1, 1, ?::timestamptz, ?::timestamptz, '[]'::jsonb)
                        """, "2026-06-01T00:00:00+00:00", D1);
        executeSql("""
                        INSERT INTO sorm_schema.invoices (id, client_id, due_date, date, items)
                        VALUES (2, 1, ?::timestamptz, ?::timestamptz, '[]'::jsonb)
                        """, "2026-01-01T00:00:00+00:00", D2);

        executeQuery("""
                query { request { invoices(filter: { dueDate: { utc: { ge: "2026-03-01T00:00:00+00:00" } } },
                    offset: 0, limit: 25) { hasNextPage result { id } } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.invoices.result.length()").isEqualTo(1)
                .jsonPath("$.data.request.invoices.result[0].id").isEqualTo("1");
    }

    @Test
    @DisplayName("orders: невалидный cursor отклоняется валидацией")
    void orders_invalidCursor_rejected() {
        executeQuery("""
                query { request { orders(filter: {}, cursor: "abc", limit: 25) { hasNextPage } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty();
    }

    private String readCursor(EntityExchangeResult<byte[]> result) {
        var body = new String(result.getResponseBody());
        var marker = "\"cursor\":\"";
        var start = body.indexOf(marker) + marker.length();
        return body.substring(start, body.indexOf('"', start));
    }
}
