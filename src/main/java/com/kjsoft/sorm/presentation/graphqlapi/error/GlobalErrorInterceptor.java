package com.kjsoft.sorm.presentation.graphqlapi.error;

import com.kjsoft.sorm.config.NotificationPropertiesConfig;
import com.kjsoft.sorm.service.system.notification.SystemNotificationService;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.util.DateUtils;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.validation.ValidationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static com.kjsoft.sorm.presentation.graphqlapi.error.Error.*;
import static com.kjsoft.sorm.service.system.notification.model.Type.MAJORERROR;
import static graphql.validation.ValidationErrorType.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalErrorInterceptor implements WebGraphQlInterceptor {
    private final SystemNotificationService systemNotificationService;
    private final DateUtils date;
    private final NotificationPropertiesConfig notificationConfig;


    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        return chain.next(request)
                .flatMap(response -> {
                    if (response.isValid()) {
                        return Mono.just(response);
                    }

                    var originalErrors = response.getExecutionResult().getErrors();
                    return Flux.fromIterable(originalErrors)
                            .flatMap(this::mapToCustomError)
                            .collect(Collectors.toList())
                            .map(transformedErrors ->
                                    response.transform(builder -> builder.errors(transformedErrors))
                            );
                });
    }

    private Mono<graphql.GraphQLError> mapToCustomError(graphql.GraphQLError error) {
        var type = error.getErrorType();

        var errorCode = INTERNAL_SERVER_ERROR;
        if (error instanceof ValidationError er) {
            errorCode = toErrorByValidationError(er);
        } else if (type != null) {
            var typeStr = type.getClass().getSimpleName();
            if (typeStr.equals("ValidationErrorType")) {
                errorCode = BAD_USER_INPUT;
            } else {
                errorCode = switch (type.toString()) {
                    case "InvalidSyntax" -> GRAPHQL_PARSE_FAILED;
                    case "OperationNotSupported", "MissingRootType" -> OPERATION_RESOLUTION_FAILURE;
                    case "ExecutionAborted" -> isWeightRequest(error) ? REQUEST_TOO_COMPLEX : INTERNAL_SERVER_ERROR;
                    default -> (type instanceof Error e) ? e : INTERNAL_SERVER_ERROR;
                };
            }
        }

        var graphQlError = buildError(errorCode, error);
        return !notificationConfig.isErrorsGraphqlApi() ? Mono.empty() :
                systemNotificationService.push(buildErrorSystemNotify())
                        .thenReturn(graphQlError)
                        .doOnError(e -> log.error("An unexpected error occurred when sending the notify", e))
                        .onErrorResume(e -> Mono.just(graphQlError));
    }

    private GraphQLError buildError(Error errorCode, graphql.GraphQLError error) {
        return GraphqlErrorBuilder.newError()
                .message(error.getMessage())
                .locations(error.getLocations())
                .path(error.getPath())
                .errorType(errorCode)
                .build();
    }

    private SystemNotification buildErrorSystemNotify() {
        return SystemNotification.builder()
                .message("Непредвиденная ошибка")
                .type(MAJORERROR)
                .createdAt(date.now())
                .build();
    }

    private boolean isWeightRequest(graphql.GraphQLError error) {
        return error.getMessage().contains("query depth") || error.getMessage().contains("query complexity");
    }

    private Error toErrorByValidationError(ValidationError error) {
        if (error.getValidationErrorType() != null) {
            return switch (error.getValidationErrorType()) {
                case UnknownOperation -> OPERATION_RESOLUTION_FAILURE;
                case InvalidSyntax -> GRAPHQL_PARSE_FAILED;
                case NullValueForNonNullArgument, BadValueForDefaultArg, VariableTypeMismatch,
                     WrongType -> BAD_USER_INPUT;
                case FieldUndefined, UnknownType, FragmentTypeConditionInvalid, UnknownDirective, UndefinedFragment,
                     InvalidFragmentType, NonExecutableDefinition -> GRAPHQL_VALIDATION_FAILED;
                default -> BAD_REQUEST;
            };
        }

        return BAD_REQUEST;
    }
}