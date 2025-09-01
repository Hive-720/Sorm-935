package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientProduct;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ClientProductPresentationMapper {

    @Mapping(target = "regDate", source = "regDate", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "nextDueDate", source = "nextDueDate", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "lastUpdate", source = "lastUpdate", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "domain", source = "domain", qualifiedByName = "stringToDomainNameBase")
    @Mapping(target = "dedicatedIpAddress", source = "dedicatedIpAddress", qualifiedByName = "stringToIpAddressBase")
    @Mapping(target = "assignedIpAddresses", source = "assignedIpAddresses", qualifiedByName = "stringListToIpAddressBase")
    ClientProduct map(com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct clientProduct);
}