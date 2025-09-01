package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Order;

import java.util.List;


public record OrderResult(String cursor, Boolean hasNextPage, List<Order> result) {
}