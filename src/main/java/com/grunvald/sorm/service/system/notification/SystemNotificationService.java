package com.grunvald.sorm.service.system.notification;

import com.grunvald.sorm.service.system.notification.model.MetricAlerts;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface SystemNotificationService {

    Flux<SystemNotification> listen();

    Mono<Void> pushMetricAlerts(MetricAlerts alerts);

    Mono<Void> push(SystemNotification notification);
}