package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Product;

import java.util.List;


public record ProductResult(String cursor, Boolean hasNextPage, List<Product> result) {
}