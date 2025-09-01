package com.kjsoft.sorm.service.whmcsdata.dataservice.email.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class Email {
    private Long id;
    private Long clientId;
    private String subject;
    private String messageBody;
    private String to;
    private List<String> cc;
    private List<String> bcc;
    private OffsetDateTime date;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}