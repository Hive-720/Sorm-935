package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionProviderMapper {

    InvoiceAdditionalDataProviderDto.TransactionDto map(GetInvoiceResponse.Transactions.Transaction transaction);
}