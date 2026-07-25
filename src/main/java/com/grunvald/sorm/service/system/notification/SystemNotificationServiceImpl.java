package com.grunvald.sorm.service.system.notification;

import com.grunvald.sorm.service.event.EventPublisher;
import com.grunvald.sorm.service.system.notification.model.MetricAlerts;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class SystemNotificationServiceImpl implements SystemNotificationService {
    private final EventPublisher eventPublisher;
    private final DateUtils date;


    @Override
    public Flux<SystemNotification> listen() {
        return eventPublisher.getStream();
    }

    @Override
    public Mono<Void> pushMetricAlerts(MetricAlerts alerts) {
        return eventPublisher.publish(SystemNotification.builder()
                        .type(Type.METRICALERTS)
                        .message("Alert from metrics")
                        .createdAt(date.now())
                        .metricAlerts(alerts)
                        .build()
                )
                .then();
    }

    @Override
    public Mono<Void> push(SystemNotification notify) {
        return eventPublisher.publish(notify)
                .then();
    }
}