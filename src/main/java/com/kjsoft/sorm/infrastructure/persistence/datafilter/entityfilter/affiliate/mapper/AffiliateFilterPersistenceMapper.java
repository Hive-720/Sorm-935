package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.AffiliatePersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateFilterPersistenceMapper {

    AffiliatePersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.AffiliateFilter filter);
}