package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientLogin;

import java.util.List;


public record ClientLoginResult(String cursor, Boolean hasNextPage, List<ClientLogin> result) {
}