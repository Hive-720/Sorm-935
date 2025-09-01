package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientGroup;

import java.util.List;


public record ClientGroupResult(String cursor, Boolean hasNextPage, List<ClientGroup> result) {
}