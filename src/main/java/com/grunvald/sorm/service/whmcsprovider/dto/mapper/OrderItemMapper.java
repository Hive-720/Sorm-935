package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "lineItem.product", target = "productName")
    OrderItem map(OrderProviderDto.LineItem lineItem, Long orderId);
}