package com.kjsoft.sorm.service.system.metadata;

import reactor.core.publisher.Mono;


public interface SystemMetadataRepository {

    Mono<SystemMetadata> addMetadata(SystemMetadata metadata);

    Mono<SystemMetadata> updateMetadata(SystemMetadata metadata);

    Mono<SystemMetadata> findByComponent(String component);
}