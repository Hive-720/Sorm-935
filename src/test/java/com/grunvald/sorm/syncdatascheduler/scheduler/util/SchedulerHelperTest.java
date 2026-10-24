package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.support.CronExpression;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerHelperTest {

    @Mock
    private SystemNotificationService notifyService;
    @Mock
    private NotificationPropertiesConfig notificationConfig;
    @Mock
    private DateUtils date;

    private SchedulerHelper helper;

    @BeforeEach
    void setUp() {
        helper = new SchedulerHelper(notifyService, notificationConfig, date);
    }

    @Test
    void createRetry_retriesWithDelays_thenStops() {
        var retry = helper.createRetry(List.of(10, 10), SchedulerType.PAYMENT_METHOD);

        StepVerifier.withVirtualTime(() -> Flux.error(new RuntimeException("boom")).retryWhen(retry))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(20))
                .verifyComplete();
    }

    @Test
    void createRetry_successfulThirdAttempt_completes() {
        var retry = helper.createRetry(List.of(10, 10, 10), SchedulerType.PAYMENT_METHOD);
        var attempts = new int[]{0};

        StepVerifier.withVirtualTime(() -> Flux.defer(() -> {
                    if (attempts[0]++ < 2) {
                        return Flux.error(new RuntimeException("boom"));
                    }
                    return Flux.just("ok");
                }).retryWhen(retry))
                .expectSubscription()
                .thenAwait(Duration.ofMillis(20))
                .expectNext("ok")
                .verifyComplete();
    }

    @Test
    void handleError_pushesMajorErrorNotification_whenEnabled() {
        when(notificationConfig.isErrorsScheduler()).thenReturn(true);
        when(date.now()).thenReturn(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
        when(notifyService.push(any())).thenReturn(Mono.empty());

        helper.handleError(new RuntimeException("task failed"), SchedulerType.EMAIL);

        var captor = ArgumentCaptor.forClass(com.grunvald.sorm.service.system.notification.model.SystemNotification.class);
        verify(notifyService, timeout(1000)).push(captor.capture());
        assertEquals(Type.MAJORERROR, captor.getValue().getType());
    }

    @Test
    void handleError_disabled_noNotification() {
        when(notificationConfig.isErrorsScheduler()).thenReturn(false);

        helper.handleError(new RuntimeException("task failed"), SchedulerType.EMAIL);

        verify(notifyService, never()).push(any());
    }

    @Test
    void handleFailure_pushesCriticalErrorNotification() {
        when(notificationConfig.isErrorsScheduler()).thenReturn(true);
        when(date.now()).thenReturn(java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC));
        when(notifyService.push(any())).thenReturn(Mono.empty());

        helper.handleFailure(new RuntimeException("fatal"), SchedulerType.EMAIL);

        var captor = ArgumentCaptor.forClass(com.grunvald.sorm.service.system.notification.model.SystemNotification.class);
        verify(notifyService, timeout(1000)).push(captor.capture());
        assertEquals(Type.CRITICALERROR, captor.getValue().getType());
    }

    @Test
    void delayByCron_emitsAfterCronDelay() {
        var cron = CronExpression.parse("* * * * * *");

        StepVerifier.withVirtualTime(() -> helper.delayByCron(cron, SchedulerType.PAYMENT_METHOD))
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(2))
                .expectNext(0L)
                .verifyComplete();
    }
}
