package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверка отсутствия N+1 в GraphQL: {@link ConnectionFactory} оборачивается
 * прокси, считающим число созданных SQL-выражений (createStatement) через R2DBC.
 * Liquibase ходит по JDBC и не учитывается; seed-данные тоже пишутся по JDBC.
 *
 * Если бы DataLoader-батчинг не работал, для N пользователей с клиентами
 * выполнилось бы не менее N + 1 SQL-запросов (по одному на каждую связь).
 */
class GraphQLNPlusOneIntegrationTest extends SormIntegrationTest {

    private static final int USERS = 10;
    private static final int CLIENTS_PER_USER = 2;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AtomicInteger sqlStatementCounter;

    @TestConfiguration
    static class SqlCountingConfig {

        @Bean
        AtomicInteger sqlStatementCounter() {
            return new AtomicInteger();
        }

        /**
         * Обёртка над авто-конфигурируемым ConnectionFactory: счётчик увеличивается
         * при каждом createStatement на выдаваемых фабрикой соединениях.
         */
        @Bean
        BeanPostProcessor countingConnectionFactoryPostProcessor(AtomicInteger sqlStatementCounter) {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    if (bean instanceof ConnectionFactory factory) {
                        return new CountingConnectionFactory(factory, sqlStatementCounter);
                    }
                    return bean;
                }
            };
        }
    }

    static class CountingConnectionFactory implements ConnectionFactory {

        private final ConnectionFactory delegate;
        private final AtomicInteger counter;

        CountingConnectionFactory(ConnectionFactory delegate, AtomicInteger counter) {
            this.delegate = delegate;
            this.counter = counter;
        }

        @Override
        public Publisher<? extends Connection> create() {
            return Flux.from(delegate.create()).map(c -> countingProxy(c, counter));
        }

        @Override
        public ConnectionFactoryMetadata getMetadata() {
            return delegate.getMetadata();
        }

        // вызывается Spring'ом (destroyMethod="dispose" у пула) при закрытии контекста
        public void dispose() {
            try {
                delegate.getClass().getMethod("dispose").invoke(delegate);
            } catch (NoSuchMethodException ignored) {
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Не удалось освободить ConnectionFactory", e);
            }
        }
    }

    private static Connection countingProxy(Connection delegate, AtomicInteger counter) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, method, args) -> {
                    if ("createStatement".equals(method.getName())) {
                        counter.incrementAndGet();
                    }
                    return method.invoke(delegate, args);
                });
    }

    @Test
    @DisplayName("users с вложенными clients: SQL-выражений меньше числа пользователей (батчинг, нет N+1)")
    void usersWithClients_noNPlusOneQueries() throws SQLException {
        seedUsersWithClients();

        sqlStatementCounter.set(0);

        executeQuery("""
                query { request { users(filter: {}, offset: 0, limit: 25) {
                    hasNextPage result { id clients { id } }
                } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.users.result.length()").isEqualTo(USERS)
                .jsonPath("$.data.request.users.result[0].clients.length()").isEqualTo(CLIENTS_PER_USER)
                .jsonPath("$.data.request.users.result[9].clients.length()").isEqualTo(CLIENTS_PER_USER);

        System.out.println("users+clients GraphQL-запрос: SQL-выражений = " + sqlStatementCounter.get());

        assertThat(sqlStatementCounter.get())
                .as("число SQL-выражений на весь GraphQL-запрос; при N+1 было бы не меньше %d", USERS + 1)
                .isGreaterThanOrEqualTo(2)
                .isLessThan(USERS);
    }

    @Test
    @DisplayName("orders с вложенными client: SQL-выражений меньше числа заказов (батчинг, нет N+1)")
    void ordersWithClient_noNPlusOneQueries() throws SQLException {
        seedOrdersWithClients();

        sqlStatementCounter.set(0);

        executeQuery("""
                query { request { orders(filter: {}, offset: 0, limit: 25) {
                    hasNextPage result { id client { id } }
                } } }
                """)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errors").doesNotExist()
                .jsonPath("$.data.request.orders.result.length()").isEqualTo(USERS)
                .jsonPath("$.data.request.orders.result[0].client.id").isEqualTo("1")
                .jsonPath("$.data.request.orders.result[9].client.id").isEqualTo("10");

        System.out.println("orders+client GraphQL-запрос: SQL-выражений = " + sqlStatementCounter.get());

        assertThat(sqlStatementCounter.get())
                .as("число SQL-выражений на весь GraphQL-запрос; при N+1 было бы не меньше %d", USERS + 1)
                .isGreaterThanOrEqualTo(2)
                .isLessThan(USERS);
    }

    private WebTestClient.ResponseSpec executeQuery(String query) {
        return webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(30))
                .build()
                .post().uri("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", query, "variables", Map.of()))
                .exchange();
    }

    private void seedUsersWithClients() throws SQLException {
        var clientId = 1;
        for (var userId = 1; userId <= USERS; userId++) {
            executeSql("INSERT INTO sorm_schema.users (id, email, full_name) VALUES (?, ?, ?)",
                    userId, "user" + userId + "@example.com", "User " + userId);
            for (var i = 0; i < CLIENTS_PER_USER; i++) {
                insertClient(clientId, userId, "client" + clientId + "@example.com");
                executeSql("INSERT INTO sorm_schema.client_user_relations (client_id, user_id) VALUES (?, ?)",
                        clientId, userId);
                clientId++;
            }
        }
    }

    private void seedOrdersWithClients() throws SQLException {
        for (var clientId = 1; clientId <= USERS; clientId++) {
            insertClient(clientId, 1, "order-client" + clientId + "@example.com");
            insertOrder(clientId, clientId, "10.0.0." + clientId, "2026-01-01T10:00:00+00:00");
        }
    }

    private void insertClient(long id, long ownerUserId, String email) throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.clients
                        (id, owner_user_id, email, first_name, last_name, full_name, city, tax_id, date_created)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::timestamptz)
                        """,
                id, ownerUserId, email, "First" + id, "Last" + id, "Full " + id, null, null,
                "2026-01-01T10:00:00+00:00");
    }

    private void insertOrder(long id, long clientId, String ipAddress, String date) throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.orders
                        (id, client_id, ip_address, date, invoice_id, amount, status)
                        VALUES (?, ?, ?, ?::timestamptz, ?, 100.00, 'Pending')
                        """,
                id, clientId, ipAddress, date, null);
    }
}
