package com.grunvald.sorm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sorm.system.notifications")
public class NotificationPropertiesConfig {
    private boolean errorsGraphqlApi = true;
    private boolean errorsApi = true;
    private boolean errorsScheduler = true;
    private boolean graphqlSchemaVersionMismatch = true;
    private boolean reload = true;
}