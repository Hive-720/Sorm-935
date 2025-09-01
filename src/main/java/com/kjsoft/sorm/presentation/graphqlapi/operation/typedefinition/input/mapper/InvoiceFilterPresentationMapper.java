package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.InvoiceFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface InvoiceFilterPresentationMapper {

    InvoiceFilter map(InvoiceFilterInput input);
}