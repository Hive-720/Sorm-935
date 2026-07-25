package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ActivityLogProviderDto {
    private Long id;
    private Long clientId;
    private Long userId;
    private String description;
    private String username;
    private String ipAddress;
    private OffsetDateTime date;
}