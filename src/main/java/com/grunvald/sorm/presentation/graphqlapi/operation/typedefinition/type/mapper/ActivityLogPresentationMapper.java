package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ActivityLog;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ActivityLogPresentationMapper {

    @Mapping(target = "username", source = "username", qualifiedByName = "stringToLoginBase")
    @Mapping(target = "ipAddress", source = "ipAddress", qualifiedByName = "stringToIpAddressBase")
    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    ActivityLog map(com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog source);
}