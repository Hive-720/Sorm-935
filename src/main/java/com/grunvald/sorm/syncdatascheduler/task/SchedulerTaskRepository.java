package com.grunvald.sorm.syncdatascheduler.task;

import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.grunvald.sorm.syncdatascheduler.scheduler.SchedulerType;
import reactor.core.publisher.Mono;


public interface SchedulerTaskRepository {

    Mono<SchedulerTask> findBySchedulerType(SchedulerType type);

    Mono<SchedulerTask> add(SchedulerTask task);

    Mono<SchedulerTask> update(SchedulerTask task);
}