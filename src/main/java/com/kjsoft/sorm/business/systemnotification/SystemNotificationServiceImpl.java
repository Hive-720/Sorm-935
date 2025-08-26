package com.kjsoft.sorm.business.systemnotification;

import com.kjsoft.sorm.business.systemnotification.entity.MetricAlerts;
import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import com.kjsoft.sorm.business.systemnotification.entity.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SystemNotificationServiceImpl implements SystemNotificationService {

    private final SystemNotificationRepository repository;


    @Override
    public Flux<SystemNotification> listen() {
        return repository.listen();
    }

    @Override
    public Mono<Void> pushMetricAlerts(MetricAlerts alerts) {
        return repository.save(SystemNotification.builder()
                        .type(Type.METRICALERTS)
                        .startAt(LocalDateTime.now()) //TODO UTC!!!
                        .metricAlerts(alerts)
                        .build()
                )
                .then();
    }

    @Override
    public Mono<Void> push(SystemNotification notify) {
        return repository.save(notify)
                .then();
    }

    @Override
    public Mono<Void> pushAll(List<SystemNotification> notification) {
        return null;
    }

    @Override
    public Mono<Void> markAsSent(SystemNotification notify) {
        notify.setSent(true);
        return Flux.just(notify)
                .flatMap(batch -> repository.save(batch)) //TODO повторы сохранения в случае ошибки
                .then();
        //TODO все исключения тут и отлавливаем
    }
}