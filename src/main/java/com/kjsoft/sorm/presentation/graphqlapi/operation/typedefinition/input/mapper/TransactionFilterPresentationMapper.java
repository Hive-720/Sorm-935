package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.TransactionFilter;
import com.kjsoft.sorm.graphql.generated.model.TransactionFilterInputGql;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        InvoiceFilterPresentationMapper.class,
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface TransactionFilterPresentationMapper {

    TransactionFilter map(TransactionFilterInputGql input);
}