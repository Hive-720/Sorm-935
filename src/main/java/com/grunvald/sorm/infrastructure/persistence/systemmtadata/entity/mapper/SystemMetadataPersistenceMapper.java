package com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.systemmtadata.entity.SystemMetadataDb;
import com.grunvald.sorm.service.system.metadata.SystemMetadata;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SystemMetadataPersistenceMapper {

    SystemMetadataDb map(SystemMetadata metadata);

    SystemMetadata map(SystemMetadataDb metadata);
}