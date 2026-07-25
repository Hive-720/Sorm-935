package com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ActivityLog {
    private Long id;
    private Long clientId;
    private Long userId;
    private String description;
    private String username;
    private String ipAddress;
    private OffsetDateTime date;
}