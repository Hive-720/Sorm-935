package com.grunvald.sorm.presentation.graphqlapi.util;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.service.system.metadata.SystemMetadataService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.util.DigestUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SchemaVersionUtilsTest {

    private static final String SCHEMA_SDL = "type Query { request: QueryMessage }";
    private static final OffsetDateTime NOW = OffsetDateTime.parse("2026-10-24T12:00:00Z");

    private GraphQlSource graphQlSource;
    private SystemMetadataService metadataService;
    private DateUtils date;
    private SchemaPrinter schemaPrinter;
    private NotificationPropertiesConfig notificationConfig;
    private SchemaVersionUtils utils;

    private int metadataCheckCount;

    @BeforeEach
    void setUp() {
        graphQlSource = mock(GraphQlSource.class);
        metadataService = mock(SystemMetadataService.class);
        date = mock(DateUtils.class);
        schemaPrinter = mock(SchemaPrinter.class);
        notificationConfig = new NotificationPropertiesConfig();
        utils = new SchemaVersionUtils(graphQlSource, metadataService, date, schemaPrinter, notificationConfig);
        metadataCheckCount = 0;

        when(graphQlSource.schema()).thenReturn(mock(GraphQLSchema.class));
        when(schemaPrinter.print(any(GraphQLSchema.class))).thenReturn(SCHEMA_SDL);
        when(date.now()).thenReturn(NOW);
        when(metadataService.checkingAndRestoringIntegrity(any(), any(), any())).thenReturn(Mono.empty());
    }

    @Test
    void checkSchemaChanges_checksIntegrityWithComponentAndMd5HashOfPrintedSchema() {
        utils.checkSchemaChanges();

        String expectedHash = DigestUtils.md5DigestAsHex(SCHEMA_SDL.getBytes(StandardCharsets.UTF_8));
        verify(metadataService).checkingAndRestoringIntegrity(eq("GRAPHQL_SCHEMA"), eq(expectedHash), any());
        verify(schemaPrinter).print(graphQlSource.schema());
    }

    @Test
    void checkSchemaChanges_createsSchemaChangedNotificationWhenEnabled() {
        utils.checkSchemaChanges();

        ArgumentCaptor<SystemNotification> captor = ArgumentCaptor.forClass(SystemNotification.class);
        verify(metadataService).checkingAndRestoringIntegrity(eq("GRAPHQL_SCHEMA"), any(), captor.capture());

        SystemNotification notification = captor.getValue();
        assertEquals("GraphQL схема была изменена", notification.getMessage());
        assertEquals(Type.SCHEMCHANGED, notification.getType());
        assertEquals(NOW, notification.getCreatedAt());
    }

    @Test
    void checkSchemaChanges_passesNullNotificationWhenMismatchNotificationDisabled() {
        notificationConfig.setGraphqlSchemaVersionMismatch(false);

        utils.checkSchemaChanges();

        verify(metadataService).checkingAndRestoringIntegrity(eq("GRAPHQL_SCHEMA"), any(), isNull());
    }

    @Test
    void checkSchemaChanges_hashChangesWhenSchemaChanges() {
        String hashForOriginalSchema = runAndCaptureHash();

        when(schemaPrinter.print(any(GraphQLSchema.class))).thenReturn("type Query { another: String }");
        String hashForChangedSchema = runAndCaptureHash();

        assertFalse(hashForOriginalSchema.equals(hashForChangedSchema));
    }

    @Test
    void checkSchemaChanges_doesNotThrowWhenIntegrityCheckFails() {
        when(metadataService.checkingAndRestoringIntegrity(any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("metadata unavailable")));

        assertDoesNotThrow(() -> utils.checkSchemaChanges());
    }

    private String runAndCaptureHash() {
        utils.checkSchemaChanges();
        metadataCheckCount++;

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);
        verify(metadataService, times(metadataCheckCount)).checkingAndRestoringIntegrity(
                eq("GRAPHQL_SCHEMA"), hashCaptor.capture(), any());
        return hashCaptor.getAllValues().get(metadataCheckCount - 1);
    }
}
