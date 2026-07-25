package com.grunvald.sorm.presentation.graphqlapi.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.grunvald.sorm.presentation.graphqlapi.error.Error.BAD_REQUEST;
import static com.grunvald.sorm.presentation.graphqlapi.error.Error.INTERNAL_SERVER_ERROR;


@Slf4j
@Component
public class CustomExceptionResolver implements DataFetcherExceptionResolver {

    @NonNull
    @Override
    public Mono<List<GraphQLError>> resolveException(@NonNull Throwable ex, @NonNull DataFetchingEnvironment env) {
        var unwrappedEx = unwrapException(ex);
        String message;
        Error errorCode;

        if (unwrappedEx instanceof BadRequestException e) {
            log.debug(unwrappedEx.getMessage(), unwrappedEx);
            message = e.getMessage();
            errorCode = BAD_REQUEST;
        } else {
            log.error("Unexpected error", unwrappedEx);
            message = "Something went wrong on our end. Please try again later.";
            errorCode = INTERNAL_SERVER_ERROR;
        }

        return Mono.just(List.of(
                GraphqlErrorBuilder.newError(env)
                        .message(message)
                        .errorType(errorCode)
                        .build()
        ));
    }

    private Throwable unwrapException(Throwable ex) {
        while (ex.getCause() != null && ex != ex.getCause()) {
            ex = ex.getCause();
        }
        return ex;
    }
}