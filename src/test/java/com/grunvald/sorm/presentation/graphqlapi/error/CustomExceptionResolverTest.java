package com.grunvald.sorm.presentation.graphqlapi.error;

import graphql.GraphQLError;
import graphql.execution.ExecutionStepInfo;
import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomExceptionResolverTest {

    private final CustomExceptionResolver resolver = new CustomExceptionResolver();

    @Test
    void badRequestException_resolvesToSingleBadRequestErrorWithOriginalMessage() {
        StepVerifier.create(resolver.resolveException(new BadRequestException("Invalid page size"), env()))
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    GraphQLError error = errors.get(0);
                    assertEquals("Invalid page size", error.getMessage());
                    assertEquals(Error.BAD_REQUEST, error.getErrorType());
                })
                .verifyComplete();
    }

    @Test
    void badRequestException_withNullMessage_resolvesWithoutCrashing() {
        StepVerifier.create(resolver.resolveException(new BadRequestException(null), env()))
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    GraphQLError error = errors.get(0);
                    assertEquals("Bad request", error.getMessage());
                    assertEquals(Error.BAD_REQUEST, error.getErrorType());
                })
                .verifyComplete();
    }

    @Test
    void unexpectedException_resolvesToInternalServerErrorWithGenericMessage() {
        StepVerifier.create(resolver.resolveException(new IllegalArgumentException("leaked internals"), env()))
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    GraphQLError error = errors.get(0);
                    assertEquals("Something went wrong on our end. Please try again later.", error.getMessage());
                    assertEquals(Error.INTERNAL_SERVER_ERROR, error.getErrorType());
                })
                .verifyComplete();
    }

    @Test
    void wrappedBadRequestException_isUnwrappedToBadRequest() {
        Throwable wrapped = new IllegalStateException(new BadRequestException("deep cause"));

        StepVerifier.create(resolver.resolveException(wrapped, env()))
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("deep cause", errors.get(0).getMessage());
                    assertEquals(Error.BAD_REQUEST, errors.get(0).getErrorType());
                })
                .verifyComplete();
    }

    @Test
    void wrappedNonBadRequestException_isUnwrappedToInternalServerError() {
        Throwable wrapped = new IllegalStateException(new IllegalArgumentException("boom"));

        StepVerifier.create(resolver.resolveException(wrapped, env()))
                .assertNext(errors -> {
                    assertEquals(1, errors.size());
                    assertEquals("Something went wrong on our end. Please try again later.", errors.get(0).getMessage());
                    assertEquals(Error.INTERNAL_SERVER_ERROR, errors.get(0).getErrorType());
                })
                .verifyComplete();
    }

    private static DataFetchingEnvironment env() {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getField()).thenReturn(new Field("field"));
        when(env.getExecutionStepInfo()).thenReturn(mock(ExecutionStepInfo.class));
        return env;
    }
}
