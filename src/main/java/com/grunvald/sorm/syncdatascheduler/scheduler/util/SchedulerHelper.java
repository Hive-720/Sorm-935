package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static com.grunvald.sorm.service.system.notification.model.Type.CRITICALERROR;
import static com.grunvald.sorm.service.system.notification.model.Type.MAJORERROR;


@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerHelper {
    private final SystemNotificationService notifyService;
    private final NotificationPropertiesConfig notificationConfig;
    private final DateUtils date;
    private final SyncTaskMetrics syncTaskMetrics;


    public Mono<Long> delayByCron(CronExpression cron, SchedulerType type) {
        return Mono.fromSupplier(() -> {
                    var now = ZonedDateTime.now();
                    return Duration.between(now, cron.next(now));
                })
                .flatMap(delay -> {
                    log.debug("Scheduler [{}]: waiting for the next launch: {}", type, delay);
                    return Mono.delay(delay);
                });
    }

    public void handleFailure(Throwable e, SchedulerType type) {
        syncTaskMetrics.schedulerStopped(type);
        log.error("Scheduler [{}]: an error has occurred. Scheduler has been stopped.", type, e);
        var msg = "[" + type + "]: при синхронизации данных с ИС произошла ошибка. Планировщик остановлен.";
        pushNotify(CRITICALERROR, msg, type);
    }

    public void handleError(Throwable e, SchedulerType type) {
        log.error("Scheduler [{}]: an error occurred during the task execution", type, e);
        var msg = "[" + type + "]: при синхронизации данных с ИС произошла ошибка.";
        pushNotify(MAJORERROR, msg, type);
    }

    public Retry createRetry(List<Integer> delays, SchedulerType type) {
        return Retry.from(retrySignal ->
                retrySignal.zipWith(Flux.range(1, delays.size()), (rs, attempt) -> attempt)
                        .flatMap(attempt -> {
                            Duration delay = Duration.ofMillis(delays.get(attempt - 1));
                            log.warn("Scheduler [{}]: retry attempt {}/{}. Waiting for: {} ms",
                                    type, attempt, delays.size(), delay.toMillis());
                            return Mono.delay(delay);
                        })
        );
    }

    private void pushNotify(Type notifyType, String msg, SchedulerType type) {
        if (notificationConfig.isErrorsScheduler()) {
            notifyService.push(SystemNotification.builder()
                            .type(notifyType)
                            .message(msg)
                            .createdAt(date.now())
                            .build()
                    )
                    .doOnError(e -> log.error("Scheduler [{}]: an unexpected error occurred when sending the notify", type, e))
                    .subscribe();
        }
    }
}