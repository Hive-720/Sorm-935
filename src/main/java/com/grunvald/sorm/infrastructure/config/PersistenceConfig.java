package com.grunvald.sorm.infrastructure.config;

import com.querydsl.sql.PostgreSQLTemplates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PersistenceConfig {

    @Bean
    public com.querydsl.sql.Configuration postgresSqlSerializerConfig() {
        var templates = PostgreSQLTemplates.builder().build();
        return new com.querydsl.sql.Configuration(templates);
    }
}
