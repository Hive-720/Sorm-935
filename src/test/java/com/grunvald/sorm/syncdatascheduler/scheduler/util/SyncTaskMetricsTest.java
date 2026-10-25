package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import io.micrometer.core.instrument.observation.DefaultMeterObservationHandler;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType.PAYMENT_METHOD;
import static org.assertj.core.api.Assertions.assertThat;

class SyncTaskMetricsTest {

    private SimpleMeterRegistry meterRegistry;
    private SyncTaskMetrics metrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        var observationRegistry = ObservationRegistry.create();
        observationRegistry.observationConfig().observationHandler(new DefaultMeterObservationHandler(meterRegistry));
        metrics = new SyncTaskMetrics(meterRegistry, observationRegistry);
    }

    @Test
    void counters_incrementWithSchedulerTag() {
        metrics.taskStarted(PAYMENT_METHOD);
        metrics.taskStarted(PAYMENT_METHOD);
        metrics.taskCompleted(PAYMENT_METHOD);
        metrics.taskFailed(PAYMENT_METHOD);
        metrics.schedulerStopped(PAYMENT_METHOD);

        assertThat(meterRegistry.get("sorm.sync.task.started").tag("scheduler", "PaymentMethod").counter().count()).isEqualTo(2);
        assertThat(meterRegistry.get("sorm.sync.task.completed").tag("scheduler", "PaymentMethod").counter().count()).isEqualTo(1);
        assertThat(meterRegistry.get("sorm.sync.task.failed").tag("scheduler", "PaymentMethod").counter().count()).isEqualTo(1);
        assertThat(meterRegistry.get("sorm.sync.scheduler.stopped").tag("scheduler", "PaymentMethod").counter().count()).isEqualTo(1);
    }

    @Test
    void startTaskObservation_recordsTimerWithSchedulerTag() {
        var observation = metrics.startTaskObservation(PAYMENT_METHOD);
        observation.stop();

        var timer = meterRegistry.find("sorm.sync.task.duration").tag("scheduler", "PaymentMethod").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }
}
