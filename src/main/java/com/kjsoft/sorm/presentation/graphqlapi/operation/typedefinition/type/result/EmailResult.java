package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.result;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Email;

import java.util.List;


public record EmailResult(String cursor, Boolean hasNextPage, List<Email> result) {
}