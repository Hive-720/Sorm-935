package com.grunvald.sorm.infrastructure.persistence.data.contact.entity;

import lombok.Data;

import java.time.OffsetDateTime;


@Data
public class ContactDb {
    private Long id;
    private Long clientId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String companyName;
    private String email;
    private String address;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String msisdn;
    private String taxId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean deleted;
}