package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ClientShortProviderDto {
    private Long id;
    private Long groupId;
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private String status;
    private OffsetDateTime dateCreated;
}