package com.kjsoft.sorm.presentation.graphql.error;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolver;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

//TODO доработать
@Component
public class CustomExceptionResolver implements DataFetcherExceptionResolver {

    @Override
    public Mono<List<GraphQLError>> resolveException(Throwable ex, DataFetchingEnvironment env) {
        var unwrappedEx = unwrapException(ex);
        return Mono.just(List.of(
                GraphqlErrorBuilder.newError(env)
                        .message("Handled by resolver: " + unwrappedEx.getMessage())
                        .errorType(ErrorType.INTERNAL_ERROR)
                        .build()
        ));

        //return Mono.empty();
    }

    private Throwable unwrapException(Throwable ex) {
        while (ex.getCause() != null && ex != ex.getCause()) {
            ex = ex.getCause();
        }
        return ex;
    }
}