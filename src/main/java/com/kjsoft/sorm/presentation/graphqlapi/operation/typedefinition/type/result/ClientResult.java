package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;

import java.util.List;


public record ClientResult(String cursor, Boolean hasNextPage, List<Client> result) {
}