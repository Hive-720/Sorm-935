package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ActivityLogMapper {

    ActivityLog map(ActivityLogProviderDto dto);
}