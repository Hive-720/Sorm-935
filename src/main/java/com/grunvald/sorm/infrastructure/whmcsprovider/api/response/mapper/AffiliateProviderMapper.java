package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetAffiliatesResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateProviderMapper {

    AffiliateProviderDto map(GetAffiliatesResponse.Affiliate affiliate);
}