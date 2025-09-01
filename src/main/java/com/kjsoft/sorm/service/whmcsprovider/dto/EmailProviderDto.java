package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class EmailProviderDto {
    private Long id;
    private Long clientId;
    private String subject;
    private String messageBody;
    private OffsetDateTime date;
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}