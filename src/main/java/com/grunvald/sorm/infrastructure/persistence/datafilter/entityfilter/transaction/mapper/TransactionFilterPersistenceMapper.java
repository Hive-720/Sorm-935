package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.TransactionPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionFilterPersistenceMapper {

    TransactionPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter filter);
}