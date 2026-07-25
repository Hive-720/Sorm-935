package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateMapper {

    Affiliate map(AffiliateProviderDto dto);
}