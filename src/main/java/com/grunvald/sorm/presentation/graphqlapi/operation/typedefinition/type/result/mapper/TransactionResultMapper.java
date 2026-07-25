package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.TransactionResult;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TransactionPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = TransactionPresentationMapper.class)
public interface TransactionResultMapper {

    @Mapping(target = "result", source = "transactionList")
    TransactionResult map(TransactionSearchResult result);
}