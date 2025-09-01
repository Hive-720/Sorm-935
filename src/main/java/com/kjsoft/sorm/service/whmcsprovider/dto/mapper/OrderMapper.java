package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.order.model.Order;
import com.kjsoft.sorm.service.whmcsprovider.dto.OrderProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order map(OrderProviderDto dto);
}