package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Credit;

import java.util.List;


public record CreditResult(String cursor, Boolean hasNextPage, List<Credit> result) {
}