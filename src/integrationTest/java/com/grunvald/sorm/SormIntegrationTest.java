package com.grunvald.sorm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * База интеграционных тестов: поднимает PostgreSQL в testcontainers,
 * Liquibase применяет миграции при старте приложения, тесты ходят по HTTP.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SormIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17.6")
            .withDatabaseName("sorm_test")
            .withUsername("postgres")
            .withPassword("test")
            .withInitScript("init/create-schema.sql");

    static {
        // старт в статическом блоке: DynamicPropertySource отрабатывает раньше,
        // чем @Testcontainers успел бы запустить контейнер
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        var host = POSTGRES.getHost();
        var port = POSTGRES.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);

        registry.add("spring.r2dbc.url",
                () -> "r2dbc:postgresql://%s:%d/%s?currentSchema=sorm_schema".formatted(host, port, POSTGRES.getDatabaseName()));
        registry.add("spring.r2dbc.username", POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES::getPassword);

        registry.add("spring.liquibase.url",
                () -> "jdbc:postgresql://%s:%d/%s".formatted(host, port, POSTGRES.getDatabaseName()));
        registry.add("spring.liquibase.user", POSTGRES::getUsername);
        registry.add("spring.liquibase.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void cleanDatabase() throws SQLException {
        try (var connection = openConnection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    TRUNCATE TABLE
                        sorm_schema.activity_logs,
                        sorm_schema.affiliates,
                        sorm_schema.client_groups,
                        sorm_schema.client_logins,
                        sorm_schema.client_products,
                        sorm_schema.client_user_relations,
                        sorm_schema.clients,
                        sorm_schema.contacts,
                        sorm_schema.credits,
                        sorm_schema.emails,
                        sorm_schema.invoices,
                        sorm_schema.order_items,
                        sorm_schema.orders,
                        sorm_schema.payment_methods,
                        sorm_schema.product_config_options,
                        sorm_schema.product_config_relations,
                        sorm_schema.product_configs,
                        sorm_schema.products,
                        sorm_schema.transactions,
                        sorm_schema.users,
                        sorm_schema.system_notifications,
                        sorm_schema.system_metadata,
                        sorm_schema.sync_tasks
                    RESTART IDENTITY CASCADE
                    """);
        }
    }

    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    protected void executeSql(String sql, Object... params) throws SQLException {
        try (var connection = openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    protected int countRows(String table) throws SQLException {
        try (var connection = openConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM sorm_schema." + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    protected void insertPaymentMethod(long id, String module, String displayName, String updatedAt, boolean deleted)
            throws SQLException {
        try (var connection = openConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO sorm_schema.payment_methods (id, module, display_name, updated_at, deleted) VALUES (?, ?, ?, ?::timestamptz, ?)")) {
            ps.setLong(1, id);
            ps.setString(2, module);
            ps.setString(3, displayName);
            ps.setString(4, updatedAt);
            ps.setBoolean(5, deleted);
            ps.executeUpdate();
        }
    }
}
