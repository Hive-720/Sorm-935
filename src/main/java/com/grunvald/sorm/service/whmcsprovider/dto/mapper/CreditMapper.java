package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsprovider.dto.CreditProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditMapper {

    Credit map(CreditProviderDto dto, Long clientId);
}