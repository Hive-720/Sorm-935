package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PaymentMethodPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.PaymentMethodResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = PaymentMethodPresentationMapper.class)
public interface PaymentMethodResultMapper {

    @Mapping(target = "result", source = "paymentMethodList")
    PaymentMethodResult map(PaymentMethodSearchResult result);
}