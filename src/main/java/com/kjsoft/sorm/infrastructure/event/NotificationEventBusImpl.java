package com.kjsoft.sorm.infrastructure.event;

import com.kjsoft.sorm.infrastructure.persistence.systemnotification.SystemNotificationRepository;
import com.kjsoft.sorm.service.event.EventPublisher;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


//TODO: можно сделать scheduler для проверки бд на наличие неотправленных и перебрасывать их в sink при наличии подключенца
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventBusImpl implements EventPublisher {
    private final Sinks.Many<SystemNotification> sink = Sinks.many().multicast().directBestEffort();
    private final SystemNotificationRepository systemNotificationRepository;


    @Override
    public Mono<Boolean> publish(SystemNotification notification) {
        return systemNotificationRepository.add(notification)
                .map(savedEntity -> {
                    var result = sink.tryEmitNext(savedEntity);

                    if (result.isFailure()) {
                        log.debug("Failed to emit notification to sink: {}", result);
                    }
                    return true;
                });
    }

    @Override
    public Flux<SystemNotification> getStream() {
        return sink.asFlux()
                .startWith(Flux.defer(systemNotificationRepository::findAllBySentIsFalse))
                .distinct(SystemNotification::getId)
                .flatMap(this::markAsSent) //TODO сработает единожжы?
                .share();
    }

    public Mono<SystemNotification> markAsSent(SystemNotification notify) {
        notify.setSent(true);
        return systemNotificationRepository.update(notify);
    }
}