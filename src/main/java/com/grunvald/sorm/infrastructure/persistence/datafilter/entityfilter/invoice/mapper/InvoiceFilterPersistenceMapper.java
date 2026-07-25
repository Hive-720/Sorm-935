package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvoiceFilterPersistenceMapper {

    InvoicePersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter filter);
}