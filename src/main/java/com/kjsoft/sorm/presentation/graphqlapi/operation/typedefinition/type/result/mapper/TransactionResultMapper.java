package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result.TransactionResult;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TransactionPresentationMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = TransactionPresentationMapper.class)
public interface TransactionResultMapper {

    @Mapping(target = "result", source = "transactionList")
    TransactionResult map(TransactionSearchResult result);
}