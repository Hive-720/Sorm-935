package com.kjsoft.sorm.infrastructure.persistence.schedulertask.entity.mapper;

import com.kjsoft.sorm.infrastructure.persistence.schedulertask.entity.SchedulerTaskDb;
import com.kjsoft.sorm.syncdatascheduler.task.model.SchedulerTask;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SyncTaskPersistenceMapper {

    SchedulerTask map(SchedulerTaskDb task);

    SchedulerTaskDb map(SchedulerTask task);
}