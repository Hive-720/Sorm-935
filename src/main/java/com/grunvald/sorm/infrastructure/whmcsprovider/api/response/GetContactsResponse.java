package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.OnlyDigitsDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;


public class GetContactsResponse extends Response {
    private Contacts contacts;

    public List<Contacts.Contact> getContacts() {
        return contacts != null && contacts.contact != null ? contacts.contact : List.of();
    }

    @Getter
    public static class Contacts {
        private List<Contact> contact;

        @Getter
        public static class Contact {
            private Long id;
            @JsonProperty("userid")
            private Long clientId;
            @JsonProperty("firstname")
            private String firstName;
            @JsonProperty("lastname")
            private String lastName;
            @JsonProperty("companyname")
            private String companyName;
            private String email;
            private String address1;
            private String address2;
            private String city;
            private String state;
            private String postcode;
            private String country;
            @JsonProperty("phonenumber")
            @JsonDeserialize(using = OnlyDigitsDeserializer.class)
            private String phoneNumber;
            @JsonProperty("tax_id")
            private String taxId;
            @JsonProperty("subaccount")
            private int subAccount;
            private String password;
            private String permissions;
            @JsonProperty("domainemails")
            private int domainEmails;
            @JsonProperty("generalemails")
            private int generalEmails;
            @JsonProperty("invoiceemails")
            private int invoiceEmails;
            @JsonProperty("productemails")
            private int productEmails;
            @JsonProperty("supportemails")
            private int supportEmails;
            @JsonProperty("affiliateemails")
            private int affiliateEmails;
            @JsonProperty("pwresetkey")
            private String pwResetKey;
            @JsonProperty("created_at")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime createdAt;
            @JsonProperty("updated_at")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime updatedAt;
            @JsonProperty("pwresetexpiry")
            private String pwResetExpiry;
        }
    }
}