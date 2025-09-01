package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.kjsoft.sorm.service.whmcsprovider.dto.CreditProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditMapper {

    Credit map(CreditProviderDto dto, Long clientId);
}