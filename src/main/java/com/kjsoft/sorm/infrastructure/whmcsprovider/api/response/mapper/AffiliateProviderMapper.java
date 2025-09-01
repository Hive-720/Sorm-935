package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetAffiliatesResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateProviderMapper {

    AffiliateProviderDto map(GetAffiliatesResponse.Affiliate affiliate);
}