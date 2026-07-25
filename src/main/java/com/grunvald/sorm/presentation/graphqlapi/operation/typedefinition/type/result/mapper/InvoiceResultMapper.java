package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.InvoiceResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.InvoicePresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = InvoicePresentationMapper.class)
public interface InvoiceResultMapper {

    @Mapping(target = "result", source = "invoiceList")
    InvoiceResult map(InvoiceSearchResult result);
}