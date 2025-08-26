package com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface SystemNotificationRepositoryDb extends ReactiveCrudRepository<SystemNotificationDB, Long> {

    Flux<SystemNotificationDB> findBySentFalse();

}