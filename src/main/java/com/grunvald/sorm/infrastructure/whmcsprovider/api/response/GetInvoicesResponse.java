package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.TrimDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


public class GetInvoicesResponse extends Response {
    public Invoices invoices;

    public List<Invoice> getInvoices() {
        return invoices != null && invoices.invoice != null ? invoices.invoice : List.of();
    }

    public static class Invoices {
        public List<Invoice> invoice;
    }


    @Getter
    public static class Invoice {
        private Long id;
        @JsonProperty("userid")
        private Long clientId;
        @JsonProperty("firstname")
        private String firstName;
        @JsonProperty("lastname")
        private String lastName;
        @JsonProperty("companyname")
        private String companyName;
        @JsonProperty("invoicenum")
        private String invoiceNum;
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime date;
        @JsonProperty("duedate")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime dueDate;
        @JsonProperty("datepaid")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime datePaid;
        @JsonProperty("last_capture_attempt")
        private OffsetDateTime lastCaptureAttempt;
        @JsonProperty("date_refunded")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime dateRefunded;
        @JsonProperty("date_cancelled")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime dateCancelled;
        @JsonProperty("subtotal")
        private BigDecimal subTotal;
        private BigDecimal credit;
        private BigDecimal tax;
        private BigDecimal tax2;
        private BigDecimal total;
        @JsonProperty("taxrate")
        private BigDecimal taxRate;
        @JsonProperty("taxrate2")
        private BigDecimal taxRate2;
        private String status;
        @JsonProperty("paymentmethod")
        private String paymentMethod;
        @JsonProperty("paymethodid")
        private Object payMethodId;
        private String notes;
        @JsonProperty("created_at")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime updatedAt;
        @JsonProperty("currencycode")
        @JsonDeserialize(using = TrimDeserializer.class)
        private String currencyCode;
        @JsonProperty("currencyprefix")
        private String currencyPrefix;
        @JsonProperty("currencysuffix")
        private String currencySuffix;
    }
}