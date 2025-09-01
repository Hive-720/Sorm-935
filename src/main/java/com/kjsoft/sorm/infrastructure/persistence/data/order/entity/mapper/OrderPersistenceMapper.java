package com.kjsoft.sorm.infrastructure.persistence.data.order.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.kjsoft.sorm.infrastructure.persistence.data.order.entity.OrderDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    Order map(OrderDb order);

    OrderDb map(Order order);
}