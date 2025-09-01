package com.kjsoft.sorm.service.whmcsprovider.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class ClientProviderDto {
    private Long id;
    private Long clientId;
    private Long ownerUserId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String companyName;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private Integer phoneNumber;
    private String taxId;
    private Boolean taxExempt;
    private String phonecc;
    private String defaultGateway;
    private Long groupId;
    private String status;
    private BigDecimal credit;
    private String language;
    private LastLogin lastLogin;
    private String telegram;
    private String jabber;
    private String currencyCode;


    @Getter
    @Setter
    public static class LastLogin {
        private String ipAddress;
        private OffsetDateTime date;
    }
}