package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.kjsoft.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateMapper {

    Affiliate map(AffiliateProviderDto dto);
}