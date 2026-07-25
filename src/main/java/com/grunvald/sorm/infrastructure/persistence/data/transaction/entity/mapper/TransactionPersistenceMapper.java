package com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.TransactionDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionPersistenceMapper {

    Transaction map(TransactionDb transaction);

    TransactionDb map(Transaction transaction);
}