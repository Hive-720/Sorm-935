package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.PaymentMethodPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.PaymentMethodResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = PaymentMethodPresentationMapper.class)
public interface PaymentMethodResultMapper {

    @Mapping(target = "result", source = "paymentMethodList")
    PaymentMethodResult map(PaymentMethodSearchResult result);
}