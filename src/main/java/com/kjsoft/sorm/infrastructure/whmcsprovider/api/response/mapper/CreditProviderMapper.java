package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetCreditsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.CreditProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditProviderMapper {

    CreditProviderDto map(GetCreditsResponse.Credits.Credit credit);
}