package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.AffiliatePersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliateFilterPersistenceMapper {

    AffiliatePersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter filter);
}