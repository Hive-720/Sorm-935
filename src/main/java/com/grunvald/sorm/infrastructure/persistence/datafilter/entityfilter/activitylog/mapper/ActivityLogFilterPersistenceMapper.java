package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.ActivityLogPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogFilterPersistenceMapper {

    ActivityLogPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter filter);
}