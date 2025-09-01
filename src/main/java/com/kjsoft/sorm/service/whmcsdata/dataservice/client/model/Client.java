package com.kjsoft.sorm.service.whmcsdata.dataservice.client.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.kjsoft.sorm.util.EqualsUtils.isBigDecimalEqual;


@Getter
@Setter
public class Client {
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


    public boolean update(Client actual) {
        var changed = false;

        if (!Objects.equals(this.phonecc, actual.phonecc)) {
            this.phonecc = actual.phonecc;
            changed = true;
        }

        if (!Objects.equals(this.phoneNumber, actual.phoneNumber)) {
            this.phoneNumber = actual.phoneNumber;
            changed = true;
        }

        if (!Objects.equals(this.msisdn, actual.msisdn)) {
            this.msisdn = actual.msisdn;
            changed = true;
        }

        if (!Objects.equals(this.ownerUserId, actual.ownerUserId)) {
            this.ownerUserId = actual.ownerUserId;
            changed = true;
        }

        if (!Objects.equals(this.fullName, actual.fullName)) {
            this.fullName = actual.fullName;
            changed = true;
        }

        if (!Objects.equals(this.firstName, actual.firstName)) {
            this.firstName = actual.firstName;
            changed = true;
        }

        if (!Objects.equals(this.companyName, actual.companyName)) {
            this.companyName = actual.companyName;
            changed = true;
        }

        if (!Objects.equals(this.email, actual.email)) {
            this.email = actual.email;
            changed = true;
        }

        if (!Objects.equals(this.address, actual.address)) {
            this.address = actual.address;
            changed = true;
        }

        if (!Objects.equals(this.city, actual.city)) {
            this.city = actual.city;
            changed = true;
        }

        if (!Objects.equals(this.state, actual.state)) {
            this.state = actual.state;
            changed = true;
        }

        if (!Objects.equals(this.postcode, actual.postcode)) {
            this.postcode = actual.postcode;
            changed = true;
        }

        if (!Objects.equals(this.country, actual.country)) {
            this.country = actual.country;
            changed = true;
        }

        if (!Objects.equals(this.taxId, actual.taxId)) {
            this.taxId = actual.taxId;
            changed = true;
        }

        if (!Objects.equals(this.telegram, actual.telegram)) {
            this.telegram = actual.telegram;
            changed = true;
        }

        if (!Objects.equals(this.jabber, actual.jabber)) {
            this.jabber = actual.jabber;
            changed = true;
        }

        if (!Objects.equals(this.currency, actual.currency)) {
            this.currency = actual.currency;
            changed = true;
        }

        if (!Objects.equals(this.defaultGateway, actual.defaultGateway)) {
            this.defaultGateway = actual.defaultGateway;
            changed = true;
        }

        if (!Objects.equals(this.status, actual.status)) {
            this.status = actual.status;
            changed = true;
        }

        if (!isBigDecimalEqual(this.credit, actual.credit)) {
            this.credit = actual.credit;
            changed = true;
        }

        if (!Objects.equals(this.taxExempt, actual.taxExempt)) {
            this.taxExempt = actual.taxExempt;
            changed = true;
        }

        if (!Objects.equals(this.language, actual.language)) {
            this.language = actual.language;
            changed = true;
        }

        if (!Objects.equals(deleted, actual.deleted)) {
            this.deleted = actual.deleted;
            changed = true;
        }

        return changed;
    }
}