package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.InvoiceResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = InvoicePresentationMapper.class)
public interface InvoiceResultMapper {

    @Mapping(target = "result", source = "invoiceList")
    InvoiceResult map(InvoiceSearchResult result);
}