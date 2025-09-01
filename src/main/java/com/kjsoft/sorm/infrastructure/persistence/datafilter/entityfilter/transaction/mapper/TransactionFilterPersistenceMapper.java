package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.TransactionPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionFilterPersistenceMapper {

    TransactionPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.TransactionFilter filter);
}