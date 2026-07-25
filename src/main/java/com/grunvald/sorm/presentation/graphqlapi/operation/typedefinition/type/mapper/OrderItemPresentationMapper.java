package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.OrderItem;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface OrderItemPresentationMapper {

    @Mapping(target = "domain", source = "domain", qualifiedByName = "stringToDomainNameBase")
    OrderItem map(com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem orderItem);
}