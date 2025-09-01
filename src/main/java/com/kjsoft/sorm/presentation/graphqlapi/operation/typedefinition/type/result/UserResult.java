package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.User;

import java.util.List;


public record UserResult(String cursor, Boolean hasNextPage, List<User> result) {
}