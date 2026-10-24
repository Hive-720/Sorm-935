package com.grunvald.sorm.presentation.graphqlapi.error;

import graphql.ErrorClassification;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorTest {

    @Test
    void error_implementsGraphqlErrorClassification() {
        assertTrue(ErrorClassification.class.isAssignableFrom(Error.class));
    }

    @Test
    void error_containsAllNineCodesInDeclaredOrder() {
        List<String> expected = List.of(
                "GRAPHQL_PARSE_FAILED",
                "GRAPHQL_VALIDATION_FAILED",
                "BAD_USER_INPUT",
                "OPERATION_RESOLUTION_FAILURE",
                "BAD_REQUEST",
                "REQUEST_NOT_FOUND",
                "NO_REQUEST_RESULT",
                "REQUEST_TOO_COMPLEX",
                "INTERNAL_SERVER_ERROR");

        assertEquals(expected, Arrays.stream(Error.values()).map(Error::name).toList());
    }

    @Test
    void valueOf_roundTripsEveryConstant() {
        for (Error error : Error.values()) {
            assertEquals(error, Error.valueOf(error.name()));
        }
    }
}
