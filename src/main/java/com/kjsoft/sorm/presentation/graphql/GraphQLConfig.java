package com.kjsoft.sorm.presentation.graphql;

import graphql.parser.Parser;
import graphql.schema.idl.SchemaPrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static graphql.schema.idl.SchemaPrinter.Options.defaultOptions;


@Configuration
public class GraphQLConfig {

    @Bean
    public WebGraphQlInterceptor subscriptionInterceptor(GraphQLAnalyzer analyzer) {
        return (webInput, interceptorChain) -> {
            var graphqlQuery = webInput.getDocument();
            if (analyzer.isSubscription(graphqlQuery)) {
                return Mono.error(new ResponseStatusException(
                        HttpStatus.METHOD_NOT_ALLOWED,
                        "Subscriptions are only supported over WebSocket"
                ));
            }
            return interceptorChain.next(webInput);
        };
    }

    //TODO понять что надо, что не надо
    @Bean
    public SchemaPrinter clearSchemaPrinter() {
        return new SchemaPrinter(defaultOptions()
                .includeDirectives(false)
                .includeSchemaDefinition(false)
                .includeScalarTypes(false)
        );
    }

    @Bean
    public Parser graphQLParser() {
        return new Parser();
    }
}
