package com.grunvald.sorm.infrastructure.persistence.data.order.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.order.model.OrderItem;
import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderItemDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderItemPersistenceMapper {

    OrderItem map(OrderItemDb orderItem);

    OrderItemDb map(OrderItem orderItem);
}