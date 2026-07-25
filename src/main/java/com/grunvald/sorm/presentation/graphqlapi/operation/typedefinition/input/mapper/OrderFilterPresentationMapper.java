package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.OrderFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.OrderFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        InvoiceFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface OrderFilterPresentationMapper {

    OrderFilter map(OrderFilterInput input);
}