package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.dto.OrderSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.OrderResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.OrderPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = OrderPresentationMapper.class)
public interface OrderResultMapper {

    @Mapping(target = "result", source = "orderList")
    OrderResult map(OrderSearchResult result);
}