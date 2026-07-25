package com.grunvald.sorm.service.system.metadata;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;


@Getter
@Builder
public class SystemMetadata {
    private Long id;
    private String component;
    private String hash;
    private OffsetDateTime updatedAt;

    public boolean isNew() {
        return id == null;
    }
}