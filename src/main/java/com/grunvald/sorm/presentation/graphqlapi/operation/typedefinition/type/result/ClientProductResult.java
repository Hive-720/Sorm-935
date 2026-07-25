package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientProduct;

import java.util.List;


public record ClientProductResult(String cursor, Boolean hasNextPage, List<ClientProduct> result) {
}