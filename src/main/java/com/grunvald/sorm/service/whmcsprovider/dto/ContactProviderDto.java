package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ContactProviderDto {
    private Long id;
    private Long clientId;
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String phoneNumber;
    private String taxId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}