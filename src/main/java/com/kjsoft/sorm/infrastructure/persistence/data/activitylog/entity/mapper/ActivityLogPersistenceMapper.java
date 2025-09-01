package com.kjsoft.sorm.infrastructure.persistence.data.activitylog.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.kjsoft.sorm.infrastructure.persistence.data.activitylog.entity.ActivityLogDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogPersistenceMapper {

    ActivityLog map(ActivityLogDb activityLog);

    ActivityLogDb map(ActivityLog activityLog);
}