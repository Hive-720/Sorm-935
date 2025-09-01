package com.kjsoft.sorm.syncdatascheduler.task;

import com.kjsoft.sorm.syncdatascheduler.task.model.SchedulerTask;
import com.kjsoft.sorm.syncdatascheduler.scheduler.SchedulerType;
import reactor.core.publisher.Mono;


public interface SchedulerTaskRepository {

    Mono<SchedulerTask> findBySchedulerType(SchedulerType type);

    Mono<SchedulerTask> add(SchedulerTask task);

    Mono<SchedulerTask> update(SchedulerTask task);
}