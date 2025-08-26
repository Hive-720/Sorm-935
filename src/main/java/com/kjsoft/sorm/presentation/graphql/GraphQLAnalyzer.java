package com.kjsoft.sorm.presentation.graphql;

import graphql.language.OperationDefinition;
import graphql.parser.Parser;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import static graphql.language.OperationDefinition.Operation.SUBSCRIPTION;


@Component
@RequiredArgsConstructor
public class GraphQLAnalyzer {

    private final Parser graphQLParser;

    public boolean isSubscription(String graphQLQuery) {
        if (Strings.isBlank(graphQLQuery)) {
            return false;
        }

        try {
            var document = graphQLParser.parseDocument(graphQLQuery);
            return document.getDefinitions().stream()
                    .filter(def -> def instanceof OperationDefinition)
                    .map(def -> (OperationDefinition) def)
                    .anyMatch(def -> def.getOperation() == SUBSCRIPTION);
        } catch (Exception e) {
            return false;
        }
    }
}