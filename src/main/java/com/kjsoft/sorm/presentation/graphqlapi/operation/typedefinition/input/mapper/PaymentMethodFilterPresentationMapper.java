package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.kjsoft.sorm.graphql.generated.model.PaymentMethodFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = DateTimeBaseFilterPresentationMapper.class)
public interface PaymentMethodFilterPresentationMapper {

    PaymentMethodFilter map(PaymentMethodFilterInputGql filterInput);
}