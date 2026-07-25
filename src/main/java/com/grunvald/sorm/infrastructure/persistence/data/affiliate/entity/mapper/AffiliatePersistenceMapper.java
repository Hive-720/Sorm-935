package com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.AffiliateDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AffiliatePersistenceMapper {

    Affiliate map(AffiliateDb affiliate);

    AffiliateDb map(Affiliate affiliate);
}