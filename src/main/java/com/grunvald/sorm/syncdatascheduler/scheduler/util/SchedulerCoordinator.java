package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


@Slf4j
@Component
public class SchedulerCoordinator {
    private final Sinks.Many<SchedulerType> sink = Sinks.many().replay().all();


    @EventListener(ApplicationReadyEvent.class)
    public void checkSchedulers(ApplicationReadyEvent event) {
        var context = event.getApplicationContext();

        for (var type : SchedulerType.values()) {
            var notExists = context.getBeansOfType(type.getClazz()).isEmpty();
            if (notExists) {
                log.debug("Scheduler [{}]: disabled in the properties", type);
                signalReady(type);
            }
        }
    }

    public void signalReady(SchedulerType type) {
        sink.tryEmitNext(type);
    }

    public Mono<Void> waitUploadFor(SchedulerType type) {
        return sink.asFlux()
                .filter(finishedType -> finishedType == type)
                .next()
                .then();
    }
}