package com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.schedulertask.entity.SchedulerTaskDb;
import com.grunvald.sorm.syncdatascheduler.task.model.SchedulerTask;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SyncTaskPersistenceMapper {

    SchedulerTask map(SchedulerTaskDb task);

    SchedulerTaskDb map(SchedulerTask task);
}