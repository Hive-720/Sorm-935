package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import com.grunvald.sorm.syncdatascheduler.scheduler.impl.ActivityLogSyncScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchedulerCoordinatorTest {

    private SchedulerCoordinator coordinator;

    @BeforeEach
    void setUp() {
        coordinator = new SchedulerCoordinator();
    }

    @Test
    void waitUploadFor_completesAfterSignal() {
        StepVerifier.create(coordinator.waitUploadFor(SchedulerType.PAYMENT_METHOD))
                .then(() -> coordinator.signalReady(SchedulerType.PAYMENT_METHOD))
                .verifyComplete();
    }

    @Test
    void waitUploadFor_otherTypeSignaled_doesNotComplete() {
        StepVerifier.create(coordinator.waitUploadFor(SchedulerType.PAYMENT_METHOD))
                .then(() -> coordinator.signalReady(SchedulerType.EMAIL))
                .verifyTimeout(Duration.ofMillis(300));
    }

    @Test
    void signalReadyBeforeSubscription_replayedToLateSubscriber() {
        // sink создан с replay().all(): сигнал до подписки доставляется новому подписчику
        coordinator.signalReady(SchedulerType.EMAIL);

        StepVerifier.create(coordinator.waitUploadFor(SchedulerType.EMAIL))
                .verifyComplete();
    }

    @Test
    void checkSchedulers_allBeansAbsent_signalsEveryType() {
        var spy = spy(coordinator);
        var event = mock(ApplicationReadyEvent.class);
        var context = mock(ConfigurableApplicationContext.class);
        when(event.getApplicationContext()).thenReturn(context);
        when(context.getBeansOfType(any())).thenReturn(Map.of());

        spy.checkSchedulers(event);

        verify(spy, times(SchedulerType.values().length)).signalReady(any());
        for (var type : SchedulerType.values()) {
            assertDoesNotThrow(() -> spy.waitUploadFor(type).block());
        }
    }

    @Test
    void checkSchedulers_existingBean_isNotSignalled() {
        var spy = spy(coordinator);
        var event = mock(ApplicationReadyEvent.class);
        var context = mock(ConfigurableApplicationContext.class);
        when(event.getApplicationContext()).thenReturn(context);
        when(context.getBeansOfType(any())).thenAnswer(inv -> {
            Class<?> clazz = inv.getArgument(0);
            return clazz == ActivityLogSyncScheduler.class ? Map.of("bean", new Object()) : Map.of();
        });

        spy.checkSchedulers(event);

        verify(spy, times(SchedulerType.values().length - 1)).signalReady(any());
        verify(spy, never()).signalReady(SchedulerType.ACTIVITY_LOG);
    }
}
