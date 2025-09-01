package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetInvoiceResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionProviderMapper {

    InvoiceAdditionalDataProviderDto.TransactionDto map(GetInvoiceResponse.Transactions.Transaction transaction);
}