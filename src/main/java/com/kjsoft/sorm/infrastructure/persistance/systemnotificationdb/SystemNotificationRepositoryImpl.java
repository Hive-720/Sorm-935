package com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb;

import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import com.kjsoft.sorm.business.systemnotification.SystemNotificationRepository;
import com.kjsoft.sorm.business.systemnotification.entity.Type;
import com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb.mapper.SystemNotificationMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Repository
@AllArgsConstructor
public class SystemNotificationRepositoryImpl implements SystemNotificationRepository {

    //TODO можно конечно и через sql
    //private final SystemNotificationRepositoryDb repositoryDb;

    // сценарий реального времени
    private final Sinks.Many<SystemNotification> trapSink = Sinks.many().multicast().onBackpressureBuffer(1000); //TODO буффер в конфиги

    @Override
    public Flux<SystemNotification> listen() {
        return trapSink.asFlux()
                .startWith(Flux.defer(this::getAllUnsent))
                .distinct(SystemNotification::getId);
    }

    @Override
    public Mono<SystemNotification> save(SystemNotification notify) {
        var notifyDb = SystemNotificationMapper.INSTANCE.toEntityDb(notify);
        return Mono.just(notify);
//        return repositoryDb.save(notifyDb)
//                .map(SystemNotificationMapper.INSTANCE::toEntity)
//                .doOnNext(n -> {
//                    trapSink.tryEmitNext(n);
//                    //TODO если неуспех, просто логируем?
//                });

    }

    @Override
    public Flux<SystemNotification> saveAll(List<SystemNotification> notification) {
        //TODO
        return null;
    }

    private Flux<SystemNotification> getAllUnsent() {
//        return repositoryDb.findBySentFalse()
//                .map(SystemNotificationMapper.INSTANCE::toEntity);
        return Flux.just(SystemNotificationDB.builder()
                        .id(1L)
                        .uuid(UUID.randomUUID().toString())
                        .type(Type.CRITICALERROR.name())
                        .message("critic_error")
                        .startAt(LocalDateTime.now())
                        .build()
                )
                .map(SystemNotificationMapper.INSTANCE::toEntity);


    }
}