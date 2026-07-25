package com.grunvald.sorm.infrastructure.persistence.systemnotification.entity;

import lombok.Builder;
import lombok.Getter;
import io.r2dbc.postgresql.codec.Json;

import java.time.OffsetDateTime;


@Builder
@Getter
public class SystemNotificationDB {
    private Long id;
    private boolean sent;
    private String message;
    private OffsetDateTime createdAt;
    private String type;
    private Json metricAlerts;
}