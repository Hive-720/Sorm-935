package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.kjsoft.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    PaymentMethod map(PaymentMethodProviderDto dto);
}