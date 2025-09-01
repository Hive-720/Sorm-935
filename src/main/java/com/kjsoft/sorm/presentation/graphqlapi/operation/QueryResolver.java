package com.kjsoft.sorm.presentation.graphqlapi.operation;

import com.kjsoft.sorm.presentation.config.GraphqlPropertiesConfig;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.QueryMessage;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class QueryResolver {
    private final SchemaPrinter apiSchemaPinter;
    private final GraphQlSource graphQlSource;
    private final GraphqlPropertiesConfig apiConfig;


    @QueryMapping
    public Mono<QueryMessage> request() {
        return Mono.just(new QueryMessage(UUID.randomUUID().toString(), false, apiConfig.getMaxPageSize()));
    }

    @QueryMapping
    public Mono<String> getSchema() {
        var schemaSDL = apiSchemaPinter.print(graphQlSource.schema());
        return Mono.just(schemaSDL);
    }
}