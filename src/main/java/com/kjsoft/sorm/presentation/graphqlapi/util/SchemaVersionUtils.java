package com.kjsoft.sorm.presentation.graphqlapi.util;

import com.kjsoft.sorm.config.NotificationPropertiesConfig;
import com.kjsoft.sorm.service.system.metadata.SystemMetadataService;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.service.system.notification.model.Type;
import com.kjsoft.sorm.util.DateUtils;
import graphql.schema.idl.SchemaPrinter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "sorm.graphql.schema.version-check.enabled", havingValue = "true")
@Slf4j
public class SchemaVersionUtils {

    private static final String COMPONENT = "GRAPHQL_SCHEMA";

    private final GraphQlSource graphQlSource;
    private final SystemMetadataService service;
    private final DateUtils date;
    private final SchemaPrinter schemaPrinter;
    private final NotificationPropertiesConfig notificationConfig;


    @EventListener(ApplicationReadyEvent.class)
    public void checkSchemaChanges() {
        log.info("Starting GraphQL schema integrity check...");

        var currentSchemaHash = calculateSchemaHash();
        var notify = notificationConfig.isGraphqlSchemaVersionMismatch() ? createNotification() : null;
        service.checkingAndRestoringIntegrity(COMPONENT, currentSchemaHash, notify)
                .doOnError(e -> log.error("Failed to check schema integrity", e))
                .subscribe(result -> log.info("Schema integrity check completed."));
    }

    private String calculateSchemaHash() {
        var schema = graphQlSource.schema();
        var schemaStr = schemaPrinter.print(schema);
        return DigestUtils.md5DigestAsHex(schemaStr.getBytes(StandardCharsets.UTF_8));
    }

    private SystemNotification createNotification() {
        return SystemNotification.builder()
                .message("GraphQL схема была изменена")
                .type(Type.SCHEMCHANGED)
                .createdAt(date.now())
                .build();
    }
}