package com.kjsoft.sorm.business.systemnotification;

import com.kjsoft.sorm.business.systemnotification.entity.MetricAlerts;
import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface SystemNotificationService {

    Flux<SystemNotification> listen();

    Mono<Void> pushMetricAlerts(MetricAlerts alerts);

    Mono<Void> push(SystemNotification notification);

    Mono<Void> pushAll(List<SystemNotification> notification);

    Mono<Void> markAsSent(SystemNotification notification);
}