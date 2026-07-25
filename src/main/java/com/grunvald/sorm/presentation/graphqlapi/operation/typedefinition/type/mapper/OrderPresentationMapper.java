package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface OrderPresentationMapper {

    @Mapping(target = "ipAddress", source = "ipAddress", qualifiedByName = "stringToIpAddressBase")
    @Mapping(target = "date", source = "date", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Order map(com.grunvald.sorm.service.whmcsdata.dataservice.order.model.Order order);
}