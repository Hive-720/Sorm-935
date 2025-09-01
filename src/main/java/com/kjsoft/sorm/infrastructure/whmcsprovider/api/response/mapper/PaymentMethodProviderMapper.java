package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PaymentMethodProviderMapper {

    PaymentMethodProviderDto map(GetPaymentMethodResponse.PaymentMethod paymentMethod);
}