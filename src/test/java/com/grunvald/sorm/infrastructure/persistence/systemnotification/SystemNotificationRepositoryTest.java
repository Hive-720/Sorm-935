package com.grunvald.sorm.infrastructure.persistence.systemnotification;

import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.persistence.systemnotification.entity.SystemNotificationDB;
import com.grunvald.sorm.infrastructure.persistence.systemnotification.entity.mapper.SystemNotificationPersistenceMapperImpl;
import com.grunvald.sorm.service.system.notification.model.MetricAlerts;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemNotificationRepositoryTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    private final DatabaseClient databaseClient = mock(DatabaseClient.class);
    private final SystemNotificationPersistenceMapperImpl mapper = new SystemNotificationPersistenceMapperImpl();
    private final SystemNotificationRepository repository = new SystemNotificationRepository(databaseClient, mapper);

    @BeforeEach
    void setUp() {
        MapStructTestFactory.inject(mapper, "objectMapper", new Config().objectMapper());
    }

    private MetricAlerts metricAlerts() {
        var alerts = new MetricAlerts();
        alerts.setGroupKey("group");
        alerts.setStatus(MetricAlerts.Status.FIRING);
        return alerts;
    }

    private SystemNotification notification(Long id) {
        return SystemNotification.builder()
                .id(id)
                .sent(false)
                .message("message")
                .type(Type.METRICALERTS)
                .createdAt(DATE)
                .metricAlerts(metricAlerts())
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void add_bindsAllFieldsAndMapsInsertedRow() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        var inserted = mapper.map(notification(9L));
        when(spec.bind(0, "METRICALERTS")).thenReturn(spec);
        when(spec.bind(1, "message")).thenReturn(spec);
        when(spec.bind(2, false)).thenReturn(spec);
        when(spec.bind(3, DATE)).thenReturn(spec);
        when(spec.bind(org.mockito.ArgumentMatchers.eq(4), org.mockito.ArgumentMatchers.any(io.r2dbc.postgresql.codec.Json.class)))
                .thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemNotificationDB.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(inserted));

        var result = repository.add(notification(null)).block();

        assertEquals(9L, result.getId());
        assertEquals("message", result.getMessage());
        assertEquals(Type.METRICALERTS, result.getType());
        assertEquals("group", result.getMetricAlerts().getGroupKey());
        verify(databaseClient).sql(contains("INSERT INTO system_notifications"));
        verify(spec).bind(0, "METRICALERTS");
        verify(spec).bind(1, "message");
        verify(spec).bind(2, false);
        verify(spec).bind(3, DATE);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void add_nullMessage_bindsNullInsteadOfValue() {
        var notification = notification(null);
        notification.setMessage(null);
        notification.setMetricAlerts(null);
        var db = mapper.map(notification);

        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "METRICALERTS")).thenReturn(spec);
        when(spec.bindNull(1, String.class)).thenReturn(spec);
        when(spec.bind(2, false)).thenReturn(spec);
        when(spec.bind(3, DATE)).thenReturn(spec);
        when(spec.bindNull(4, io.r2dbc.postgresql.codec.Json.class)).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemNotificationDB.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(db));

        var result = repository.add(notification).block();

        assertEquals(null, result.getMessage());
        assertEquals(null, result.getMetricAlerts());
        verify(spec).bindNull(1, String.class);
        verify(spec).bindNull(4, io.r2dbc.postgresql.codec.Json.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void update_bindsFieldsAndId() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        var updated = mapper.map(notification(9L));
        when(spec.bind(0, "METRICALERTS")).thenReturn(spec);
        when(spec.bind(1, "message")).thenReturn(spec);
        when(spec.bind(2, false)).thenReturn(spec);
        when(spec.bind(org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.any(io.r2dbc.postgresql.codec.Json.class)))
                .thenReturn(spec);
        when(spec.bind(4, 9L)).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemNotificationDB.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(updated));

        var result = repository.update(notification(9L)).block();

        assertEquals(9L, result.getId());
        verify(databaseClient).sql(contains("UPDATE system_notifications"));
        verify(spec).bind(4, 9L);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllBySentIsFalse_mapsEveryRow() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        var first = mapper.map(notification(1L));
        var second = mapper.map(notification(2L));
        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemNotificationDB.class)).thenReturn(rows);
        when(rows.all()).thenReturn(Flux.just(first, second));

        var result = repository.findAllBySentIsFalse().collectList().block();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(databaseClient).sql(contains("WHERE sent = false"));
        verify(rows).all();
    }
}
