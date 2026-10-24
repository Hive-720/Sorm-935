package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import com.grunvald.sorm.service.whmcsdata.datasyncservice.paymentmethod.PaymentMethodSyncService;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты синхронизации с внешней системой (WHMCS):
 * выгрузка платёжных методов по HTTP (form-urlencoded) и применение данных
 * к нашей БД — добавление новых записей и обновление существующих.
 *
 * Внешняя система имитируется MockWebServer'ом, отдающим JSON-фикстуру;
 * планировщики отключены, синхронизация запускается вручную через
 * {@link PaymentMethodSyncService}.
 */
class PaymentMethodSyncIntegrationTest extends SormIntegrationTest {

    private static final String INITIAL_FIXTURE = """
            {"result":"success","paymentmethods":{"paymentmethod":[
                {"module":"mailin","displayname":"Mailin"},
                {"module":"stripe","displayname":"Stripe"}
            ]}}
            """;

    private static final String UPDATE_FIXTURE = """
            {"result":"success","paymentmethods":{"paymentmethod":[
                {"module":"mailin","displayname":"Mailin New"},
                {"module":"paypal","displayname":"PayPal"}
            ]}}
            """;

    private static final MockWebServer WHMCS = new MockWebServer();

    private static final AtomicReference<String> FIXTURE = new AtomicReference<>();

    private static final List<String> REQUEST_BODIES = new CopyOnWriteArrayList<>();

    static {
        WHMCS.setDispatcher(new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                REQUEST_BODIES.add(request.getBody().readUtf8());
                return new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json")
                        .setBody(FIXTURE.get());
            }
        });
        try {
            WHMCS.start();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось поднять mock WHMCS", e);
        }
    }

    @DynamicPropertySource
    static void registerWhmcsProperties(DynamicPropertyRegistry registry) {
        registry.add("sorm.whmcs.client.url", () -> WHMCS.url("/whmcs").toString());
        registry.add("sorm.whmcs.credentials.identifier", () -> "test-identifier");
        registry.add("sorm.whmcs.credentials.secret", () -> "test-secret");
        // включаем клиент WHMCS, но выключаем все фоновые планировщики:
        // синхронизацию тест запускает сам, фоновые задачи не должны вмешиваться
        registry.add("sorm.whmcs.data_sync.enabled", () -> "true");
        registry.add("sorm.whmcs.data_sync.schedulers.activity_log.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.affiliate.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.client.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.client_details.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.client_group.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.client_product.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.contact.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.contact_delete.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.credit.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.email.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.invoice.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.order.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.product.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.user.enabled", () -> "false");
    }

    @Autowired
    private PaymentMethodSyncService paymentMethodSyncService;

    @BeforeEach
    void resetMock() {
        REQUEST_BODIES.clear();
    }

    @Test
    @DisplayName("выгрузка из WHMCS добавляет новые платёжные методы в пустую БД")
    void syncFromWhmcs_addsNewPaymentMethods() throws SQLException {
        FIXTURE.set(INITIAL_FIXTURE);

        var actual = paymentMethodSyncService.getActualData().block();

        assertThat(actual).hasSize(2);
        assertThat(actual.getFirst().getModule()).isEqualTo("mailin");
        assertThat(actual.getFirst().getDisplayName()).isEqualTo("Mailin");
        assertThat(actual.getFirst().getId()).isNull();
        assertThat(actual.getFirst().isDeleted()).isFalse();

        paymentMethodSyncService.applyActualData(actual).block();

        assertThat(countRows("payment_methods")).isEqualTo(2);
        assertThat(readPaymentMethodRows()).containsExactly(
                "mailin|Mailin|false|true",
                "stripe|Stripe|false|true");
    }

    @Test
    @DisplayName("повторная выгрузка обновляет существующие записи, добавляет новые и помечает удалённые")
    void syncFromWhmcs_updatesExistingAndMarksDeleted() throws SQLException {
        // засев без явных id: GENERATED BY DEFAULT AS IDENTITY не сдвигает sequence
        // при явном указании id, и следующая identity-вставка получила бы конфликт
        insertPaymentMethodWithoutId("mailin", "Old Mailin");
        insertPaymentMethodWithoutId("stripe", "Stripe");

        FIXTURE.set(UPDATE_FIXTURE);

        var actual = paymentMethodSyncService.getActualData().block();
        paymentMethodSyncService.applyActualData(actual).block();

        // mailin обновлён, paypal добавлен (id генерирует identity), stripe помечен удалённым
        assertThat(countRows("payment_methods")).isEqualTo(3);
        assertThat(readPaymentMethodRows()).containsExactly(
                "mailin|Mailin New|false|true",
                "stripe|Stripe|true|true",
                "paypal|PayPal|false|true");
    }

    @Test
    @DisplayName("повторная выгрузка с теми же данными идемпотентна — дубликатов нет")
    void syncFromWhmcs_sameDataTwice_isIdempotent() throws SQLException {
        FIXTURE.set(INITIAL_FIXTURE);

        paymentMethodSyncService.applyActualData(paymentMethodSyncService.getActualData().block()).block();
        paymentMethodSyncService.applyActualData(paymentMethodSyncService.getActualData().block()).block();

        assertThat(countRows("payment_methods")).isEqualTo(2);
        assertThat(readPaymentMethodRows()).containsExactly(
                "mailin|Mailin|false|true",
                "stripe|Stripe|false|true");
    }

    @Test
    @DisplayName("клиент шлёт WHMCS корректный form-запрос GetPaymentMethods")
    void syncFromWhmcs_sendsGetPaymentMethodsForm() throws SQLException {
        FIXTURE.set(INITIAL_FIXTURE);

        paymentMethodSyncService.getActualData().block();

        assertThat(REQUEST_BODIES).singleElement().satisfies(body ->
                assertThat(body)
                        .contains("action=GetPaymentMethods")
                        .contains("identifier=test-identifier")
                        .contains("secret=test-secret")
                        .contains("responsetype=json"));
    }

    private void insertPaymentMethodWithoutId(String module, String displayName) throws SQLException {
        executeSql("""
                        INSERT INTO sorm_schema.payment_methods (module, display_name, updated_at, deleted)
                        VALUES (?, ?, ?::timestamptz, ?)
                        """,
                module, displayName, "2026-01-01 10:00:00+00", false);
    }

    private List<String> readPaymentMethodRows() throws SQLException {
        var rows = new ArrayList<String>();
        try (var connection = openConnection();
             var ps = connection.prepareStatement("""
                     SELECT module, display_name, deleted, updated_at
                     FROM sorm_schema.payment_methods
                     ORDER BY id
                     """);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(rs.getString("module") + "|" + rs.getString("display_name")
                        + "|" + rs.getBoolean("deleted")
                        + "|" + (rs.getTimestamp("updated_at") != null));
            }
        }
        return rows;
    }
}
