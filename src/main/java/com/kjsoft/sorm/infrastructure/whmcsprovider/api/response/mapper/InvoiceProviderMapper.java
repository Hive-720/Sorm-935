package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetInvoicesResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface InvoiceProviderMapper {

    InvoiceProviderDto map(GetInvoicesResponse.Invoice invoice);
}