package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;


@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "dto.currencyCode", target = "currency")
    Invoice map(InvoiceProviderDto dto, BigDecimal balance, List<InvoiceAdditionalDataProviderDto.Item> items);
}