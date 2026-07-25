package com.grunvald.sorm.service.event;

import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface EventPublisher {

    Mono<Boolean> publish(SystemNotification notification);

    Flux<SystemNotification> getStream();
}