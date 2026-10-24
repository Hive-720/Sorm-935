package com.grunvald.sorm.service.system.notification;

import com.grunvald.sorm.service.event.EventPublisher;
import com.grunvald.sorm.service.system.notification.model.MetricAlerts;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemNotificationServiceImplTest {

    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private DateUtils date;

    private SystemNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SystemNotificationServiceImpl(eventPublisher, date);
    }

    @Test
    void listen_returnsPublisherStream() {
        var notification = SystemNotification.builder().message("m").build();
        when(eventPublisher.getStream()).thenReturn(Flux.just(notification));

        StepVerifier.create(service.listen())
                .expectNext(notification)
                .verifyComplete();
    }

    @Test
    void push_publishesAsIs() {
        var notification = SystemNotification.builder()
                .message("restarted")
                .type(Type.RESTARTDB)
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();
        when(eventPublisher.publish(notification)).thenReturn(Mono.just(true));

        StepVerifier.create(service.push(notification))
                .verifyComplete();

        verify(eventPublisher).publish(notification);
    }

    @Test
    void pushMetricAlerts_wrapsAlertsInNotification() {
        var alerts = new MetricAlerts();
        when(date.now()).thenReturn(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC));
        when(eventPublisher.publish(org.mockito.ArgumentMatchers.any())).thenReturn(Mono.just(true));

        StepVerifier.create(service.pushMetricAlerts(alerts))
                .verifyComplete();

        var captor = org.mockito.ArgumentCaptor.forClass(SystemNotification.class);
        verify(eventPublisher).publish(captor.capture());
        var published = captor.getValue();
        assertEquals(Type.METRICALERTS, published.getType());
        assertEquals(alerts, published.getMetricAlerts());
        assertEquals(OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), published.getCreatedAt());
    }
}
