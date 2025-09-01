package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.ActivityLogPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogFilterPersistenceMapper {

    ActivityLogPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ActivityLogFilter filter);
}