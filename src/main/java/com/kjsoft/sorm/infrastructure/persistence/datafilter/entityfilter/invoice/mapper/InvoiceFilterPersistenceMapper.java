package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvoiceFilterPersistenceMapper {

    InvoicePersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.InvoiceFilter filter);
}