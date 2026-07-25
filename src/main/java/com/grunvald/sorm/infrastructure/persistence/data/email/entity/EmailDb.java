package com.grunvald.sorm.infrastructure.persistence.data.email.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class EmailDb {
    private Long id;
    private Long clientId;
    private String subject;
    private String messageBody;
    private String to;
    private String[] cc;
    private String[] bcc;
    private OffsetDateTime date;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}