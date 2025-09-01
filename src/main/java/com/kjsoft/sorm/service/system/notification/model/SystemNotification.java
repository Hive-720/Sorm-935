package com.kjsoft.sorm.service.system.notification.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
@Builder
public class SystemNotification {
    private Long id;
    private boolean sent;
    private String message;
    private Type type;
    private OffsetDateTime createdAt;
    private MetricAlerts metricAlerts;
}