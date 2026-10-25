package com.grunvald.sorm.syncdatascheduler.scheduler.util;

import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

@Component
public class SyncTaskMetrics {

    private static final String TASK_DURATION = "sorm.sync.task.duration";

    private final MeterRegistry meterRegistry;
    private final ObservationRegistry observationRegistry;

    public SyncTaskMetrics(MeterRegistry meterRegistry, ObservationRegistry observationRegistry) {
        this.meterRegistry = meterRegistry;
        this.observationRegistry = observationRegistry;
    }

    public void taskStarted(SchedulerType schedulerType) {
        counter("sorm.sync.task.started", schedulerType).increment();
    }

    public void taskCompleted(SchedulerType schedulerType) {
        counter("sorm.sync.task.completed", schedulerType).increment();
    }

    public void taskFailed(SchedulerType schedulerType) {
        counter("sorm.sync.task.failed", schedulerType).increment();
    }

    public void schedulerStopped(SchedulerType schedulerType) {
        counter("sorm.sync.scheduler.stopped", schedulerType).increment();
    }

    public Observation startTaskObservation(SchedulerType schedulerType) {
        return Observation.createNotStarted(TASK_DURATION, observationRegistry)
                .lowCardinalityKeyValue("scheduler", schedulerType.getCommonName())
                .start();
    }

    private Counter counter(String name, SchedulerType schedulerType) {
        return Counter.builder(name)
                .tag("scheduler", schedulerType.getCommonName())
                .register(meterRegistry);
    }
}
