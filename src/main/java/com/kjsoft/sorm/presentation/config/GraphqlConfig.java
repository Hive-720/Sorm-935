package com.kjsoft.sorm.presentation.config;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.parser.Parser;
import graphql.schema.DefaultGraphqlTypeComparatorRegistry;
import graphql.schema.idl.SchemaPrinter;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;


@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class GraphqlConfig {
    private final GraphqlPropertiesConfig propertiesConfig;


    @Bean
    public SchemaPrinter schemaPinter() {
        var comparatorRegistry = DefaultGraphqlTypeComparatorRegistry.defaultComparators();

        var options = SchemaPrinter.Options.defaultOptions()
                .includeDirectives(true)
                .includeScalarTypes(true)
                .includeSchemaDefinition(true)
                .setComparators(comparatorRegistry);

        return new SchemaPrinter(options);
    }

    @Bean
    public Parser graphQLParser() {
        return new Parser();
    }

    @Bean
    public ValidationRules validationRules() {
        return ValidationRules.newValidationRules()
                .build();
    }

    @Bean
    public ValidationSchemaWiring validationSchemaWiring(ValidationRules validationRules) {
        return new ValidationSchemaWiring(validationRules);
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(ValidationSchemaWiring validationSchemaWiring) {
        return wiringBuilder -> wiringBuilder
                .directiveWiring(validationSchemaWiring);
    }

    @Bean
    public Instrumentation complexityInstrumentation() {
        return new MaxQueryComplexityInstrumentation(propertiesConfig.getQuery().getAnalysis().getMaxComplexity());
    }

    @Bean
    public Instrumentation depthInstrumentation() {
        return new MaxQueryDepthInstrumentation(propertiesConfig.getQuery().getAnalysis().getMaxDepth());
    }
}