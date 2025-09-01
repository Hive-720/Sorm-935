package com.kjsoft.sorm.infrastructure.persistence.systemmtadata.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class SystemMetadataDb {
    private Long id;
    private String component;
    private String hash;
    private OffsetDateTime updatedAt;
}