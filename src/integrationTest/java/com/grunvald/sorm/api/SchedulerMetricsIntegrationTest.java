package com.grunvald.sorm.api;

import com.grunvald.sorm.SormIntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Проверка метрик планировщиков: включается только payment_method
 * (ONLY_FIRST с короткой задержкой), WHMCS имитируется MockWebServer'ом.
 */
class SchedulerMetricsIntegrationTest extends SormIntegrationTest {

    private static final String FIXTURE = """
            {"result":"success","paymentmethods":{"paymentmethod":[
                {"module":"mailin","displayname":"Mailin"}
            ]}}
            """;

    private static final MockWebServer WHMCS = new MockWebServer();

    static {
        try {
            WHMCS.start();
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось поднять mock WHMCS", e);
        }
        // Задержка ответа: первый запуск планировщика происходит из @PostConstruct
        // во время поднятия контекста, раньше чем Liquibase успевает создать схему.
        // Медленный HTTP-ответ переносит запись в БД на время после миграций
        // (и на случай повторов отвечаем несколько раз).
        for (int i = 0; i < 3; i++) {
            WHMCS.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeadersDelay(10, java.util.concurrent.TimeUnit.SECONDS)
                    .addHeader("Content-Type", "application/json")
                    .setBody(FIXTURE));
        }
    }

    @DynamicPropertySource
    static void registerSchedulerProperties(DynamicPropertyRegistry registry) {
        registry.add("sorm.whmcs.client.url", () -> WHMCS.url("/whmcs").toString());
        registry.add("sorm.whmcs.credentials.identifier", () -> "test-identifier");
        registry.add("sorm.whmcs.credentials.secret", () -> "test-secret");
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
        registry.add("sorm.whmcs.data_sync.schedulers.product.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.user.enabled", () -> "false");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.enabled", () -> "true");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.mode", () -> "ONLY_FIRST");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.cron_expression", () -> "* * * * * *");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.first_start.delays[0]", () -> "100");
        registry.add("sorm.whmcs.data_sync.schedulers.payment_method.first_start.delays[1]", () -> "1000");
    }

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DisplayName("запуск задачи синка пишет sorm.sync.task.* с тегом scheduler")
    void schedulerTaskRun_recordsSyncTaskMetrics() {
        await().atMost(Duration.ofSeconds(60)).untilAsserted(() -> {
            assertThat(meterRegistry.find("sorm.sync.task.duration")
                    .tag("scheduler", "PaymentMethod").timer()).isNotNull();
            assertThat(meterRegistry.find("sorm.sync.task.started")
                    .tag("scheduler", "PaymentMethod").counter()).isNotNull();
            assertThat(meterRegistry.find("sorm.sync.task.completed")
                    .tag("scheduler", "PaymentMethod").counter()).isNotNull();
        });

        assertThat(meterRegistry.get("sorm.sync.task.started")
                .tag("scheduler", "PaymentMethod").counter().count()).isGreaterThanOrEqualTo(1);
        assertThat(meterRegistry.get("sorm.sync.task.completed")
                .tag("scheduler", "PaymentMethod").counter().count()).isGreaterThanOrEqualTo(1);
    }
}
