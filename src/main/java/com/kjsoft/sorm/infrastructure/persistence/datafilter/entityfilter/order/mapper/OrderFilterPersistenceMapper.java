package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.OrderPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderFilterPersistenceMapper {

    OrderPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.OrderFilter filter);
}