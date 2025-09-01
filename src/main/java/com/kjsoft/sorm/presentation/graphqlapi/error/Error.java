package com.kjsoft.sorm.presentation.graphqlapi.error;

import graphql.ErrorClassification;


public enum Error implements ErrorClassification {
    GRAPHQL_PARSE_FAILED,
    GRAPHQL_VALIDATION_FAILED,
    BAD_USER_INPUT,
    OPERATION_RESOLUTION_FAILURE,
    BAD_REQUEST,
    REQUEST_NOT_FOUND,
    NO_REQUEST_RESULT,
    REQUEST_TOO_COMPLEX,
    INTERNAL_SERVER_ERROR
}