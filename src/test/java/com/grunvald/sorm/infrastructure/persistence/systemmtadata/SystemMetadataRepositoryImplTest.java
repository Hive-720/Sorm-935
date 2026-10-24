package com.grunvald.sorm.infrastructure.persistence.systemmtadata;

import com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.SystemMetadataDb;
import com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.mapper.SystemMetadataPersistenceMapperImpl;
import com.grunvald.sorm.service.system.metadata.SystemMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemMetadataRepositoryImplTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    private final DatabaseClient databaseClient = mock(DatabaseClient.class);
    private final SystemMetadataRepositoryImpl repository =
            new SystemMetadataRepositoryImpl(databaseClient, new SystemMetadataPersistenceMapperImpl());

    private SystemMetadata metadata(Long id) {
        return SystemMetadata.builder()
                .id(id)
                .component("component")
                .hash("hash")
                .updatedAt(DATE)
                .build();
    }

    @Test
    @SuppressWarnings("unchecked")
    void addMetadata_bindsComponentHashUpdatedAtAndMapsInsertedRow() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "component")).thenReturn(spec);
        when(spec.bind(1, "hash")).thenReturn(spec);
        when(spec.bind(2, DATE)).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemMetadataDb.class)).thenReturn(rows);
        var inserted = new SystemMetadataDb();
        inserted.setId(7L);
        inserted.setComponent("component");
        inserted.setHash("hash");
        inserted.setUpdatedAt(DATE);
        when(rows.one()).thenReturn(Mono.just(inserted));

        var result = repository.addMetadata(metadata(null)).block();

        assertEquals(7L, result.getId());
        assertEquals("component", result.getComponent());
        assertEquals("hash", result.getHash());
        assertEquals(DATE, result.getUpdatedAt());
        verify(databaseClient).sql(contains("INSERT INTO system_metadata"));
        verify(spec).bind(0, "component");
        verify(spec).bind(1, "hash");
        verify(spec).bind(2, DATE);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void addMetadata_emptyResult_returnsEmptyMono() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "component")).thenReturn(spec);
        when(spec.bind(1, "hash")).thenReturn(spec);
        when(spec.bind(2, DATE)).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemMetadataDb.class)).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.empty());

        assertNull(repository.addMetadata(metadata(null)).block());
    }

    @Test
    @SuppressWarnings("unchecked")
    void updateMetadata_bindsHashUpdatedAtAndId() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "hash")).thenReturn(spec);
        when(spec.bind(1, DATE)).thenReturn(spec);
        when(spec.bind(2, 7L)).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemMetadataDb.class)).thenReturn(rows);
        var updated = new SystemMetadataDb();
        updated.setId(7L);
        updated.setComponent("component");
        updated.setHash("newHash");
        updated.setUpdatedAt(DATE);
        when(rows.one()).thenReturn(Mono.just(updated));

        var result = repository.updateMetadata(metadata(7L)).block();

        assertEquals("newHash", result.getHash());
        verify(databaseClient).sql(contains("UPDATE system_metadata"));
        verify(spec).bind(0, "hash");
        verify(spec).bind(1, DATE);
        verify(spec).bind(2, 7L);
        verify(rows).one();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByComponent_usesFirstAndMapsRow() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "component")).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemMetadataDb.class)).thenReturn(rows);
        var found = new SystemMetadataDb();
        found.setId(7L);
        found.setComponent("component");
        found.setHash("hash");
        found.setUpdatedAt(DATE);
        when(rows.first()).thenReturn(Mono.just(found));

        var result = repository.findByComponent("component").block();

        assertEquals(7L, result.getId());
        assertEquals("hash", result.getHash());
        verify(databaseClient).sql(contains("WHERE component = $1 LIMIT 1"));
        verify(spec).bind(0, "component");
        verify(rows).first();
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByComponent_emptyResult_returnsEmptyMono() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(0, "component")).thenReturn(spec);

        var rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(SystemMetadataDb.class)).thenReturn(rows);
        when(rows.first()).thenReturn(Mono.empty());

        assertNull(repository.findByComponent("component").block());
    }
}
