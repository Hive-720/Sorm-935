package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.kjsoft.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction map(InvoiceAdditionalDataProviderDto.TransactionDto dto, String currency);
}