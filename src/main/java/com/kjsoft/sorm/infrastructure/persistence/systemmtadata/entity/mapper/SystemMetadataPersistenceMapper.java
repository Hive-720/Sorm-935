package com.kjsoft.sorm.infrastructure.persistence.systemmtadata.entity.mapper;

import com.kjsoft.sorm.infrastructure.persistence.systemmtadata.entity.SystemMetadataDb;
import com.kjsoft.sorm.service.system.metadata.SystemMetadata;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface SystemMetadataPersistenceMapper {

    SystemMetadataDb map(SystemMetadata metadata);

    SystemMetadata map(SystemMetadataDb metadata);
}