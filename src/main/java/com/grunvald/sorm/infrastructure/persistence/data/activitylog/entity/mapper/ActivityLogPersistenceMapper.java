package com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.ActivityLogDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogPersistenceMapper {

    ActivityLog map(ActivityLogDb activityLog);

    ActivityLogDb map(ActivityLog activityLog);
}