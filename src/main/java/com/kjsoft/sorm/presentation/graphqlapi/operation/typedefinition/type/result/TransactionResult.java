package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;

import java.util.List;


public record TransactionResult(String cursor, Boolean hasNextPage, List<Transaction> result) {
}