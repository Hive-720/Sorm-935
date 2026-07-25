package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.TransactionFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        InvoiceFilterPresentationMapper.class,
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface TransactionFilterPresentationMapper {

    TransactionFilter map(TransactionFilterInput input);
}