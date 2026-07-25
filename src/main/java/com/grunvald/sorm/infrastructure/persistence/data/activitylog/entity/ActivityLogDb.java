package com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ActivityLogDb {
    private Long id;
    private Long clientId;
    private Long userId;
    private String description;
    private String username;
    private String ipAddress;
    private OffsetDateTime date;
}