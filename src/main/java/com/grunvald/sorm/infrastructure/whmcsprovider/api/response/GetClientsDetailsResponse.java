package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.CurrencyCodeToAlpha3Deserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsLastLoginDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
public class GetClientsDetailsResponse extends Response {
    @JsonProperty("userid")
    private Long userid; //userid=clientId
    @JsonProperty("client_id")
    private Long clientId;
    private Long id;
    @JsonProperty("owner_user_id")
    private Long ownerUserId;
    private String uuid;
    @JsonProperty("firstname")
    private String firstName;
    @JsonProperty("lastname")
    private String lastName;
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("companyname")
    private String companyName;
    private String email;
    private String address1;
    private String address2;
    private String city;
    @JsonProperty("fullstate")
    private String fullState;
    private String state;
    private String postcode;
    @JsonProperty("countrycode")
    private String countryCode;
    private String country;
    @JsonProperty("phonenumber")
    private String phoneNumber;
    @JsonProperty("tax_id")
    private String taxId;
    @JsonProperty("email_preferences")
    private EmailPreferences emailPreferences;
    @JsonProperty("statecode")
    private String stateCode;
    @JsonProperty("countryname")
    private String countryName;
    @JsonProperty("phonecc")
    private String phonecc;
    @JsonProperty("phonenumberformatted")
    private String phoneNumberFormatted;
    private String telephoneNumber;
    @JsonProperty("billingcid")
    private int billingCId;
    private String notes;
    private int currency;
    @JsonProperty("defaultgateway")
    private String defaultGateway;
    @JsonProperty("cctype")
    private String ccType;
    @JsonProperty("cclastfour")
    private String ccLastFour;
    @JsonProperty("gatewayid")
    private String gatewayId;
    @JsonProperty("groupid")
    private Long groupId;
    private String status;
    private BigDecimal credit;
    @JsonProperty("taxexempt")
    private boolean taxExempt;
    @JsonProperty("latefeeoveride")
    private boolean lateFeeOverride;
    @JsonProperty("overideduenotices")
    private boolean overrideDueNotices;
    @JsonProperty("separateinvoices")
    private boolean separateInvoices;
    @JsonProperty("disableautocc")
    private boolean disableAutoCc;
    @JsonProperty("emailoptout")
    private boolean emailOptOut;
    @JsonProperty("marketing_emails_opt_in")
    private boolean marketingEmailsOptIn;
    @JsonProperty("overrideautoclose")
    private boolean overrideAutoClose;
    private int allowSingleSignOn;
    @JsonProperty("email_verified")
    private boolean emailVerified;
    private String language;
    private boolean isOptedInToMarketingEmails;
    @JsonProperty("lastlogin")
    @JsonDeserialize(using = WhmcsLastLoginDeserializer.class)
    private LastLogin lastLogin;
    @JsonProperty("customfields1")
    private String customfields1;
    @JsonProperty("customfields")
    private List<CustomField> customFields;
    @JsonProperty("customfields2")
    private String customfields2;
    @JsonProperty("customfields3")
    private String customfields3;
    @JsonProperty("customfields4")
    private String customfields4;
    @JsonProperty("customfields5")
    private String telegram;
    @JsonProperty("customfields6")
    private String jabber;
    @JsonProperty("customfields7")
    private String customfields7;
    @JsonProperty("currency_code")
    @JsonDeserialize(using = CurrencyCodeToAlpha3Deserializer.class)
    private Integer currencyCode;
    private Users users;
    private Client client;

    private static class EmailPreferences {
        private int general;
        private int invoice;
        private int support;
        private int product;
        private int domain;
        private int affiliate;
    }

    private static class CustomField {
        private Long id;
        private String value;
    }

    private static class Users {
        private List<User> user;
    }

    private static class User {
        private Long id;
        private String name;
        private String email;
        @JsonProperty("is_owner")
        private boolean isOwner;
    }

    @Getter
    public static class Client {
        private Long id;
        @JsonProperty("userid")
        private Long userid;
        @JsonProperty("client_id")
        private Long clientId;
        @JsonProperty("owner_user_id")
        private Long ownerUserId;
        private String uuid;
        @JsonProperty("firstname")
        private String firstName;
        @JsonProperty("lastname")
        private String lastName;
        @JsonProperty("fullname")
        private String fullName;
        @JsonProperty("companyname")
        private String companyName;
        private String email;
        private String address1;
        private String address2;
        private String city;
        @JsonProperty("fullstate")
        private String fullState;
        private String state;
        private String postcode;
        @JsonProperty("countrycode")
        private String countryCode;
        private String country;
        @JsonProperty("phonenumber")
        private String phoneNumber;
        @JsonProperty("tax_id")
        private String taxId;
        @JsonProperty("email_preferences")
        private EmailPreferences emailPreferences;
        @JsonProperty("statecode")
        private String stateCode;
        @JsonProperty("countryname")
        private String countryName;
        @JsonProperty("phonecc")
        private String phoneCc;
        @JsonProperty("phonenumberformatted")
        private String phoneNumberFormatted;
        private String telephoneNumber;
        @JsonProperty("billingcid")
        private int billingCId;
        private String notes;
        private int currency;
        @JsonProperty("defaultgateway")
        private String defaultGateway;
        @JsonProperty("groupid")
        private Long groupId;
        private String status;
        private BigDecimal credit;
        @JsonProperty("taxexempt")
        private boolean taxExempt;
        @JsonProperty("latefeeoveride")
        private boolean lateFeeOverride;
        @JsonProperty("overideduenotices")
        private boolean overrideDueNotices;
        @JsonProperty("separateinvoices")
        private boolean separateInvoices;
        @JsonProperty("disableautocc")
        private boolean disableAutoCc;
        @JsonProperty("emailoptout")
        private boolean emailOptOut;
        @JsonProperty("marketing_emails_opt_in")
        private boolean marketingEmailsOptIn;
        @JsonProperty("overrideautoclose")
        private boolean overrideAutoClose;
        private int allowSingleSignOn;
        @JsonProperty("email_verified")
        private boolean emailVerified;
        private String language;
        private boolean isOptedInToMarketingEmails;
        @JsonProperty("lastlogin")
        @JsonDeserialize(using = WhmcsLastLoginDeserializer.class)
        private LastLogin lastLogin;
        @JsonProperty("customfields1")
        private String customfields1;
        @JsonProperty("customfields")
        private List<CustomField> customFields;
        @JsonProperty("customfields2")
        private String customfields2;
        @JsonProperty("customfields3")
        private String customfields3;
        @JsonProperty("customfields4")
        private String customfields4;
        @JsonProperty("customfields5")
        private String telegram;
        @JsonProperty("customfields6")
        private String jabber;
        @JsonProperty("customfields7")
        private String customfields7;
        @JsonProperty("currency_code")
        @JsonDeserialize(using = CurrencyCodeToAlpha3Deserializer.class)
        private String currencyCode;
        private Users users;
    }

    @Getter
    @Setter
    public static class LastLogin {
        private OffsetDateTime date;
        private String ipAddress;
    }
}