package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit.CreditFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditFilterPersistenceMapper {

    CreditFilter map(com.grunvald.sorm.service.whmcsdata.filter.CreditFilter filter);
}