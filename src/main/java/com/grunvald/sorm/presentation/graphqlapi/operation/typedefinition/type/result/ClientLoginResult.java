package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ClientLogin;

import java.util.List;


public record ClientLoginResult(String cursor, Boolean hasNextPage, List<ClientLogin> result) {
}