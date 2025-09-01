package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.credit.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.credit.CreditFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CreditFilterPersistenceMapper {

    CreditFilter map(com.kjsoft.sorm.service.whmcsdata.filter.CreditFilter filter);
}