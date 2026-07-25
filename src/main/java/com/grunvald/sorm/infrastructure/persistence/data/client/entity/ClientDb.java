package com.grunvald.sorm.infrastructure.persistence.data.client.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class ClientDb {
    private Long id;
    private Long clientGroupId;
    private Long ownerUserId;
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
    private Integer phonecc;
    private String phoneNumber;
    private String taxId;
    private String telegram;
    private String jabber;
    private String currency;
    private String defaultGateway;
    private String status;
    private BigDecimal credit;
    private Boolean taxExempt;
    private String language;
    private OffsetDateTime dateCreated;
    private boolean deleted;
}