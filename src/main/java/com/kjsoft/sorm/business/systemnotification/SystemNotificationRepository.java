package com.kjsoft.sorm.business.systemnotification;

import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


public interface SystemNotificationRepository {

    Flux<SystemNotification> listen();

    Mono<SystemNotification> save(SystemNotification notification);

    Flux<SystemNotification> saveAll(List<SystemNotification> notification);
}