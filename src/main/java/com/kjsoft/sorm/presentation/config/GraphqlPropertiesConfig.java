package com.kjsoft.sorm.presentation.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "sorm.graphql.api")
public class GraphqlPropertiesConfig {
    @Positive
    private Integer maxPageSize = 100;
    @Valid
    private QueryConfig query = QueryConfig.DEFAULT;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class QueryConfig {
        public static final QueryConfig DEFAULT = new QueryConfig(new Analysis(25, 200));

        @Valid
        private Analysis analysis;

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Analysis {
            @Positive
            private Integer maxDepth;
            @Positive
            private Integer maxComplexity;
        }
    }
}