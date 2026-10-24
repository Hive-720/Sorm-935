package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Alert;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.KeyValueRef;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.MetricAlerts;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Trap;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.AlertStatus;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.TrapType;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.trapMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrapMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));
    private static final String DATE_UTC = "2024-01-02T00:04:05+00:00";

    private TrapMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = trapMapper();
    }

    private com.grunvald.sorm.service.system.notification.model.MetricAlerts.KeyValueRef modelKv(String key, String value) {
        var kv = new com.grunvald.sorm.service.system.notification.model.MetricAlerts.KeyValueRef();
        kv.setKey(key);
        kv.setValue(value);
        return kv;
    }

    private com.grunvald.sorm.service.system.notification.model.MetricAlerts.Alert modelAlert() {
        var alert = new com.grunvald.sorm.service.system.notification.model.MetricAlerts.Alert();
        alert.setAlertStatus(com.grunvald.sorm.service.system.notification.model.MetricAlerts.Status.FIRING);
        alert.setLabels(List.of(modelKv("severity", "critical")));
        alert.setAnnotations(List.of(modelKv("summary", "down")));
        alert.setStartAt(DATE);
        alert.setEndsAt(DATE);
        alert.setGeneratorURL("http://prometheus/graph");
        alert.setFingerprint("fingerprint-1");
        return alert;
    }

    private com.grunvald.sorm.service.system.notification.model.MetricAlerts modelMetricAlerts() {
        var metricAlerts = new com.grunvald.sorm.service.system.notification.model.MetricAlerts();
        metricAlerts.setGroupKey("group-1");
        metricAlerts.setStatus(com.grunvald.sorm.service.system.notification.model.MetricAlerts.Status.FIRING);
        metricAlerts.setReceiver("team-x");
        metricAlerts.setGroupLabels(List.of(modelKv("alertname", "down")));
        metricAlerts.setCommonLabels(List.of(modelKv("job", "node")));
        metricAlerts.setCommonAnnotations(List.of(modelKv("runbook", "doc")));
        metricAlerts.setExternalURL("http://alertmanager");
        metricAlerts.setAlerts(List.of(modelAlert()));
        return metricAlerts;
    }

    @Test
    void shouldMapSystemNotificationToTrap() {
        SystemNotification source = SystemNotification.builder()
                .id(1L)
                .sent(true)
                .message("alert fired")
                .type(Type.METRICALERTS)
                .createdAt(DATE)
                .metricAlerts(modelMetricAlerts())
                .build();

        Trap result = mapper.map(source);

        assertEquals(TrapType.METRICALERTS, result.type());
        assertEquals("alert fired", result.message());
        assertEquals(new DateTimeBase(DATE_UTC), result.startsAt());
        MetricAlerts metricalerts = result.metricalerts();
        assertEquals("group-1", metricalerts.groupKey());
        assertEquals(AlertStatus.FIRING, metricalerts.status());
        assertEquals("team-x", metricalerts.receiver());
        assertEquals(List.of(new KeyValueRef("alertname", "down")), metricalerts.groupLabels());
        assertEquals(List.of(new KeyValueRef("job", "node")), metricalerts.commonLabels());
        assertEquals(List.of(new KeyValueRef("runbook", "doc")), metricalerts.commonAnnotations());
        assertEquals("http://alertmanager", metricalerts.externalURL());
        assertEquals(1, metricalerts.alerts().size());
    }

    @Test
    void shouldMapAlertViaMapAlert() {
        Alert result = mapper.mapAlert(modelAlert());

        assertEquals(AlertStatus.FIRING, result.status());
        assertEquals(List.of(new KeyValueRef("severity", "critical")), result.labels());
        assertEquals(List.of(new KeyValueRef("summary", "down")), result.annotations());
        assertEquals(new DateTimeBase(DATE_UTC), result.startAt());
        assertEquals(new DateTimeBase(DATE_UTC), result.endsAt());
        assertEquals("http://prometheus/graph", result.generatorURL());
        assertEquals("fingerprint-1", result.fingerprint());
    }

    @Test
    void shouldMapAlertContentInsideMetricAlerts() {
        SystemNotification source = SystemNotification.builder()
                .type(Type.METRICALERTS)
                .metricAlerts(modelMetricAlerts())
                .build();

        Trap result = mapper.map(source);

        Alert alert = result.metricalerts().alerts().get(0);
        assertEquals(AlertStatus.FIRING, alert.status());
        assertEquals(List.of(new KeyValueRef("severity", "critical")), alert.labels());
        assertEquals(List.of(new KeyValueRef("summary", "down")), alert.annotations());
        assertEquals(new DateTimeBase(DATE_UTC), alert.startAt());
        assertEquals(new DateTimeBase(DATE_UTC), alert.endsAt());
        assertEquals("http://prometheus/graph", alert.generatorURL());
        assertEquals("fingerprint-1", alert.fingerprint());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }

    @Test
    void shouldMapNullAlertToNull() {
        assertNull(mapper.mapAlert(null));
    }
}
