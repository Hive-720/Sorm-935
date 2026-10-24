package com.grunvald.sorm.presentation.graphqlapi.error;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.language.SourceLocation;
import graphql.validation.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GlobalErrorInterceptorTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-10-24T12:00:00Z");

    private SystemNotificationService notificationService;
    private DateUtils date;
    private NotificationPropertiesConfig notificationConfig;
    private GlobalErrorInterceptor interceptor;

    private WebGraphQlRequest request;
    private WebGraphQlInterceptor.Chain chain;
    private WebGraphQlResponse response;
    private ExecutionResult executionResult;
    private WebGraphQlResponse.Builder responseBuilder;
    private WebGraphQlResponse transformedResponse;
    private int transformCount;

    @BeforeEach
    void setUp() {
        notificationService = mock(SystemNotificationService.class);
        date = mock(DateUtils.class);
        notificationConfig = new NotificationPropertiesConfig();
        interceptor = new GlobalErrorInterceptor(notificationService, date, notificationConfig);

        request = mock(WebGraphQlRequest.class);
        chain = mock(WebGraphQlInterceptor.Chain.class);
        response = mock(WebGraphQlResponse.class);
        executionResult = mock(ExecutionResult.class);
        responseBuilder = mock(WebGraphQlResponse.Builder.class);
        transformedResponse = mock(WebGraphQlResponse.class);
        transformCount = 0;

        when(date.now()).thenReturn(NOW);
        when(chain.next(request)).thenReturn(Mono.just(response));
        when(response.getExecutionResult()).thenReturn(executionResult);
        when(response.transform(any())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Consumer<WebGraphQlResponse.Builder> consumer = invocation.getArgument(0);
            consumer.accept(responseBuilder);
            return transformedResponse;
        });
        when(responseBuilder.errors(any())).thenReturn(responseBuilder);
        when(notificationService.push(any())).thenReturn(Mono.empty());
    }

    @Test
    void validResponse_passesThroughWithoutTouchingErrorsOrNotifications() {
        when(response.isValid()).thenReturn(true);

        StepVerifier.create(interceptor.intercept(request, chain))
                .expectNext(response)
                .verifyComplete();

        verify(response, never()).transform(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    void invalidResponse_withoutErrors_transformsToEmptyErrorList() {
        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of());

        assertTrue(transformed.isEmpty());
        verifyNoInteractions(notificationService);
    }

    @Test
    void validationError_unknownOperation_mapsToOperationResolutionFailure() {
        ValidationError original = validationError(graphql.validation.ValidationErrorType.UnknownOperation,
                "Unknown operation 'foo'");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(1, transformed.size());
        assertEquals(Error.OPERATION_RESOLUTION_FAILURE, transformed.getFirst().getErrorType());
        assertEquals("Unknown operation 'foo'", transformed.getFirst().getMessage());
        assertEquals(List.of(new SourceLocation(1, 2)), transformed.getFirst().getLocations());
    }

    @Test
    void validationError_invalidSyntax_mapsToGraphqlParseFailed() {
        ValidationError original = validationError(graphql.validation.ValidationErrorType.InvalidSyntax,
                "Invalid syntax");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.GRAPHQL_PARSE_FAILED, transformed.getFirst().getErrorType());
    }

    @Test
    void validationError_badUserInputTypes_mapToBadUserInput() {
        for (graphql.validation.ValidationErrorType type : List.of(
                graphql.validation.ValidationErrorType.NullValueForNonNullArgument,
                graphql.validation.ValidationErrorType.BadValueForDefaultArg,
                graphql.validation.ValidationErrorType.VariableTypeMismatch,
                graphql.validation.ValidationErrorType.WrongType)) {
            List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(validationError(type, "bad input")));
            assertEquals(Error.BAD_USER_INPUT, transformed.getFirst().getErrorType(), "unexpected mapping for " + type);
        }
    }

    @Test
    void validationError_validationFailedTypes_mapToGraphqlValidationFailed() {
        for (graphql.validation.ValidationErrorType type : List.of(
                graphql.validation.ValidationErrorType.FieldUndefined,
                graphql.validation.ValidationErrorType.UnknownType,
                graphql.validation.ValidationErrorType.FragmentTypeConditionInvalid,
                graphql.validation.ValidationErrorType.UnknownDirective,
                graphql.validation.ValidationErrorType.UndefinedFragment,
                graphql.validation.ValidationErrorType.InvalidFragmentType,
                graphql.validation.ValidationErrorType.NonExecutableDefinition)) {
            List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(validationError(type, "invalid")));
            assertEquals(Error.GRAPHQL_VALIDATION_FAILED, transformed.getFirst().getErrorType(),
                    "unexpected mapping for " + type);
        }
    }

    @Test
    void validationError_otherTypes_defaultToBadRequest() {
        for (graphql.validation.ValidationErrorType type : List.of(
                graphql.validation.ValidationErrorType.UnknownArgument,
                graphql.validation.ValidationErrorType.DuplicateOperationName)) {
            List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(validationError(type, "invalid")));
            assertEquals(Error.BAD_REQUEST, transformed.getFirst().getErrorType(), "unexpected mapping for " + type);
        }
    }

    @Test
    void validationError_withoutType_mapsToBadRequest() {
        ValidationError original = ValidationError.newValidationError()
                .description("type-less validation error")
                .build();

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.BAD_REQUEST, transformed.getFirst().getErrorType());
        assertEquals("type-less validation error", transformed.getFirst().getMessage());
    }

    @Test
    void errorTypeInvalidSyntax_mapsToGraphqlParseFailed() {
        GraphQLError original = graphQlError(ErrorType.InvalidSyntax, "syntax error");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.GRAPHQL_PARSE_FAILED, transformed.getFirst().getErrorType());
        assertEquals("syntax error", transformed.getFirst().getMessage());
    }

    @Test
    void errorTypeOperationNotSupported_mapsToOperationResolutionFailure() {
        GraphQLError original = graphQlError(ErrorType.OperationNotSupported, "no root type");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.OPERATION_RESOLUTION_FAILURE, transformed.getFirst().getErrorType());
    }

    @Test
    void errorTypeNamedMissingRootType_mapsToOperationResolutionFailure() {
        ErrorClassification type = mock(ErrorClassification.class);
        when(type.toString()).thenReturn("MissingRootType");
        GraphQLError original = graphQlError(type, "missing root type");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.OPERATION_RESOLUTION_FAILURE, transformed.getFirst().getErrorType());
    }

    @Test
    void errorTypeClassNamedValidationErrorType_mapsToBadUserInput() {
        GraphQLError original = mock(GraphQLError.class);
        when(original.getErrorType()).thenReturn(new ValidationErrorType());
        when(original.getMessage()).thenReturn("bad user input");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.BAD_USER_INPUT, transformed.getFirst().getErrorType());
    }

    @Test
    void executionAborted_withComplexityMessage_mapsToRequestTooComplex() {
        GraphQLError original = graphQlError(ErrorType.ExecutionAborted,
                "query complexity of 1001 exceeds max of 1000");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.REQUEST_TOO_COMPLEX, transformed.getFirst().getErrorType());
    }

    @Test
    void executionAborted_withDepthMessage_mapsToRequestTooComplex() {
        GraphQLError original = graphQlError(ErrorType.ExecutionAborted,
                "query depth of 26 exceeds max of 25");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.REQUEST_TOO_COMPLEX, transformed.getFirst().getErrorType());
    }

    @Test
    void executionAborted_withOtherMessage_mapsToInternalServerError() {
        GraphQLError original = graphQlError(ErrorType.ExecutionAborted, "execution cancelled");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.INTERNAL_SERVER_ERROR, transformed.getFirst().getErrorType());
    }

    @Test
    void customErrorEnum_passesThroughUnchanged() {
        GraphQLError original = graphQlError(Error.NO_REQUEST_RESULT, "no result");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.NO_REQUEST_RESULT, transformed.getFirst().getErrorType());
    }

    @Test
    void unmappedErrorType_mapsToInternalServerError() {
        GraphQLError original = graphQlError(ErrorType.DataFetchingException, "boom");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.INTERNAL_SERVER_ERROR, transformed.getFirst().getErrorType());
    }

    @Test
    void nullErrorType_mapsToInternalServerError() {
        GraphQLError original = mock(GraphQLError.class);
        when(original.getErrorType()).thenReturn(null);
        when(original.getMessage()).thenReturn("no classification");

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(Error.INTERNAL_SERVER_ERROR, transformed.getFirst().getErrorType());
    }

    @Test
    void multipleErrors_allAreTransformedInOrder() {
        List<GraphQLError> originals = List.of(
                validationError(graphql.validation.ValidationErrorType.UnknownOperation, "unknown"),
                graphQlError(ErrorType.InvalidSyntax, "syntax"));

        List<GraphQLError> transformed = interceptAndCaptureErrors(originals);

        assertEquals(2, transformed.size());
        assertEquals(Error.OPERATION_RESOLUTION_FAILURE, transformed.get(0).getErrorType());
        assertEquals(Error.GRAPHQL_PARSE_FAILED, transformed.get(1).getErrorType());
    }

    @Test
    void transformedError_preservesLocationsAndPath() {
        GraphQLError original = GraphqlErrorBuilder.newError()
                .message("depth exceeded")
                .errorType(ErrorType.ExecutionAborted)
                .locations(List.of(new SourceLocation(3, 4)))
                .path(List.of("clients", "edges"))
                .build();

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(original));

        assertEquals(1, transformed.size());
        assertEquals(List.of(new SourceLocation(3, 4)), transformed.getFirst().getLocations());
        assertEquals(List.of("clients", "edges"), transformed.getFirst().getPath());
    }

    @Test
    void notificationEnabled_pushesMajorErrorNotification() {
        interceptAndCaptureErrors(List.of(
                validationError(graphql.validation.ValidationErrorType.UnknownOperation, "unknown")));

        ArgumentCaptor<SystemNotification> captor = ArgumentCaptor.forClass(SystemNotification.class);
        verify(notificationService).push(captor.capture());

        SystemNotification notification = captor.getValue();
        assertEquals("Непредвиденная ошибка", notification.getMessage());
        assertEquals(Type.MAJORERROR, notification.getType());
        assertEquals(NOW, notification.getCreatedAt());
    }

    @Test
    void notificationDisabled_dropsAllErrorsAndSkipsNotificationService() {
        notificationConfig.setErrorsGraphqlApi(false);

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(
                validationError(graphql.validation.ValidationErrorType.UnknownOperation, "unknown")));

        assertTrue(transformed.isEmpty());
        verifyNoInteractions(notificationService);
    }

    @Test
    void notificationPushFailure_stillReturnsTransformedError() {
        when(notificationService.push(any())).thenReturn(Mono.error(new RuntimeException("notify broken")));

        List<GraphQLError> transformed = interceptAndCaptureErrors(List.of(
                validationError(graphql.validation.ValidationErrorType.UnknownOperation, "unknown")));

        assertEquals(1, transformed.size());
        assertEquals(Error.OPERATION_RESOLUTION_FAILURE, transformed.getFirst().getErrorType());
    }

    private List<GraphQLError> interceptAndCaptureErrors(List<GraphQLError> originalErrors) {
        when(response.isValid()).thenReturn(false);
        when(executionResult.getErrors()).thenReturn(originalErrors);

        StepVerifier.create(interceptor.intercept(request, chain))
                .expectNext(transformedResponse)
                .verifyComplete();

        transformCount++;
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GraphQLError>> captor = ArgumentCaptor.forClass(List.class);
        verify(responseBuilder, times(transformCount)).errors(captor.capture());
        return captor.getAllValues().get(transformCount - 1);
    }

    private static ValidationError validationError(graphql.validation.ValidationErrorType type, String description) {
        return ValidationError.newValidationError()
                .validationErrorType(type)
                .sourceLocation(new SourceLocation(1, 2))
                .description(description)
                .build();
    }

    private static GraphQLError graphQlError(ErrorClassification type, String message) {
        return GraphqlErrorBuilder.newError()
                .message(message)
                .errorType(type)
                .build();
    }

    private static final class ValidationErrorType implements ErrorClassification {
    }
}