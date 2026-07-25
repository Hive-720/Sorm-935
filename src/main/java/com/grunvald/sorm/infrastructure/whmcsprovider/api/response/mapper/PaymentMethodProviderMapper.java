package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PaymentMethodProviderMapper {

    PaymentMethodProviderDto map(GetPaymentMethodResponse.PaymentMethod paymentMethod);
}