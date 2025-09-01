package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.OrderFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        InvoiceFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface OrderFilterPresentationMapper {

    OrderFilter map(OrderFilterInput input);
}