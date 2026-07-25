package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoicesResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvoiceProviderMapper {

    InvoiceProviderDto map(GetInvoicesResponse.Invoice invoice);
}