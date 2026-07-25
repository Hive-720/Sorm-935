package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientLogin;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ClientLoginPresentationMapper {

    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "ipAddress", source = "ipAddress", qualifiedByName = "stringToIpAddressBase")
    ClientLogin map(com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin clientLogin);
}