package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.OrderPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderFilterPersistenceMapper {

    OrderPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.OrderFilter filter);
}