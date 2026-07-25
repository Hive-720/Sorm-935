package com.grunvald.sorm.service.whmcsdata.dataservice.contact.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

import static com.grunvald.sorm.util.EqualsUtils.isOffsetDateTimeEqual;


@Getter
@Setter
public class Contact {
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

    public boolean update(Contact actual) {
        var changed = false;

        if (!Objects.equals(this.firstName, actual.firstName)) {
            this.firstName = actual.firstName;
            changed = true;
        }

        if (!Objects.equals(this.lastName, actual.lastName)) {
            this.lastName = actual.lastName;
            changed = true;
        }

        if (!Objects.equals(this.fullName, actual.fullName)) {
            this.fullName = actual.fullName;
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

        if (!Objects.equals(this.msisdn, actual.msisdn)) {
            this.msisdn = actual.msisdn;
            changed = true;
        }

        if (!Objects.equals(this.taxId, actual.taxId)) {
            this.taxId = actual.taxId;
            changed = true;
        }

        if (!Objects.equals(postcode, actual.getPostcode())) {
            this.postcode = actual.getPostcode();
            changed = true;
        }

        if (!Objects.equals(country, actual.getCountry())) {
            this.country = actual.getCountry();
            changed = true;
        }

        if (!Objects.equals(taxId, actual.getTaxId())) {
            this.taxId = actual.getTaxId();
            changed = true;
        }

        if (!isOffsetDateTimeEqual(this.updatedAt, actual.updatedAt)) {
            this.updatedAt = actual.updatedAt;
            changed = true;
        }

        if (Objects.equals(this.deleted, actual.deleted)) {
            this.deleted = actual.deleted;
            changed = true;
        }

        return changed;
    }
}