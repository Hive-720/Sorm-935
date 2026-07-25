package com.grunvald.sorm.infrastructure.persistence.data.client.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ClientLoginDb {
    private Long id;
    private Long clientId;
    private String ipAddress;
    private OffsetDateTime date;
}