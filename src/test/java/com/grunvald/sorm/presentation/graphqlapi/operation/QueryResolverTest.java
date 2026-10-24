package com.grunvald.sorm.presentation.graphqlapi.operation;

import com.grunvald.sorm.presentation.config.GraphqlPropertiesConfig;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.QueryMessage;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.GraphQlSource;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QueryResolverTest {

    private SchemaPrinter schemaPrinter;
    private GraphQlSource graphQlSource;
    private GraphqlPropertiesConfig apiConfig;
    private QueryResolver resolver;

    @BeforeEach
    void setUp() {
        schemaPrinter = mock(SchemaPrinter.class);
        graphQlSource = mock(GraphQlSource.class);
        apiConfig = new GraphqlPropertiesConfig();
        resolver = new QueryResolver(schemaPrinter, graphQlSource, apiConfig);
    }

    @Test
    void request_returnsQueryMessageWithUuidOfflineFalseAndConfiguredPageSize() {
        apiConfig.setMaxPageSize(250);

        StepVerifier.create(resolver.request())
                .assertNext(message -> {
                    assertNotNull(message.requestID());
                    UUID.fromString(message.requestID());
                    assertFalse(message.offline());
                    assertEquals(250, message.maxPageSize());
                })
                .verifyComplete();
    }

    @Test
    void request_usesDefaultMaxPageSizeWhenNotConfigured() {
        QueryMessage message = resolver.request().block();

        assertEquals(100, message.maxPageSize());
    }

    @Test
    void request_generatesUniqueRequestIdPerCall() {
        QueryMessage first = resolver.request().block();
        QueryMessage second = resolver.request().block();

        assertNotNull(first);
        assertNotNull(second);
        assertTrue(!first.requestID().equals(second.requestID()));
    }

    @Test
    void getSchema_printsSchemaOfGraphQlSourceAndReturnsItInMono() {
        GraphQLSchema schema = mock(GraphQLSchema.class);
        when(graphQlSource.schema()).thenReturn(schema);
        when(schemaPrinter.print(schema)).thenReturn("type Query { request: QueryMessage }");

        StepVerifier.create(resolver.getSchema())
                .expectNext("type Query { request: QueryMessage }")
                .verifyComplete();

        verify(schemaPrinter).print(schema);
    }
}
