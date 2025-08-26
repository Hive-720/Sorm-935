package com.kjsoft.sorm.business.systemnotification.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class SystemNotification {

    private Long id;
    private String uuid;
    private boolean sent;
    private String message;
    private Type type;
    private LocalDateTime startAt;
    private MetricAlerts metricAlerts;
}