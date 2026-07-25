package com.grunvald.sorm.infrastructure.persistence.systemmtadata;

import com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.SystemMetadataDb;
import com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.mapper.SystemMetadataPersistenceMapper;
import com.grunvald.sorm.service.system.metadata.SystemMetadata;
import com.grunvald.sorm.service.system.metadata.SystemMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@RequiredArgsConstructor
public class SystemMetadataRepositoryImpl implements SystemMetadataRepository {
    private final DatabaseClient databaseClient;
    private final SystemMetadataPersistenceMapper mapper;


    @Override
    public Mono<SystemMetadata> addMetadata(SystemMetadata metadata) {
        var metadataDb = mapper.map(metadata);
        return databaseClient.sql("""
                        INSERT INTO system_metadata
                        (component, hash, updated_at)
                        VALUES ($1, $2, $3)
                        RETURNING *
                        """)
                .bind(0, metadataDb.getComponent())
                .bind(1, metadataDb.getHash())
                .bind(2, metadataDb.getUpdatedAt())
                .mapProperties(SystemMetadataDb.class)
                .one()
                .map(mapper::map);
    }

    @Override
    public Mono<SystemMetadata> updateMetadata(SystemMetadata metadata) {
        var metadataDb = mapper.map(metadata);
        var spec = databaseClient.sql("""
                         UPDATE system_metadata
                         SET hash = $1,
                             updated_at = $2
                         WHERE id = $3
                         RETURNING id, component, hash, updated_at
                        """)
                .bind(0, metadataDb.getHash())
                .bind(1, metadataDb.getUpdatedAt())
                .bind(2, metadataDb.getId());

        return spec
                .mapProperties(SystemMetadataDb.class)
                .one()
                .map(mapper::map);
    }

    @Override
    public Mono<SystemMetadata> findByComponent(String component) {
        return databaseClient.sql("SELECT * FROM system_metadata WHERE component = $1 LIMIT 1")
                .bind(0, component)
                .mapProperties(SystemMetadataDb.class)
                .first()
                .map(mapper::map);
    }
}