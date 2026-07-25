package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.IntegerToBooleanDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
public class GetInvoiceResponse extends Response {
    @JsonProperty("invoiceid")
    private Long id;
    @JsonProperty("invoicenum")
    private String num;
    @JsonProperty("userid")
    private Long clientId;
    @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
    private OffsetDateTime date;
    @JsonProperty("duedate")
    @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
    private OffsetDateTime dueDate;
    @JsonProperty("datepaid")
    @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
    private OffsetDateTime datePaid;
    @JsonProperty("lastcaptureattempt")
    @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
    private OffsetDateTime lastCaptureAttempt;
    @JsonProperty("subtotal")
    private BigDecimal subTotal;
    private BigDecimal credit;
    private BigDecimal tax;
    private BigDecimal tax2;
    private BigDecimal total;
    private BigDecimal balance;
    @JsonProperty("taxrate")
    private BigDecimal taxRate;
    @JsonProperty("taxrate2")
    private BigDecimal taxRate2;
    private String status;
    @JsonProperty("paymentmethod")
    private String paymentMethod;
    private String notes;
    @JsonProperty("ccgateway")
    private boolean ccGateway;
    private Items items;
    private Transactions transactions;

    public List<Items.Item> getItems() {
        return items != null && items.item != null ? items.item : List.of();
    }

    public List<Transactions.Transaction> getTransactions() {
        return transactions != null && transactions.transactions != null ? transactions.transactions : List.of();
    }

    @Getter
    public static class Items {
        private List<Item> item;

        @Getter
        public static class Item {
            private Long id;
            private String type;
            @JsonProperty("relid")
            private int relId;
            private String description;
            private BigDecimal amount;
            @JsonDeserialize(using = IntegerToBooleanDeserializer.class)
            private Boolean taxed;
        }
    }

    @Getter
    public static class Transactions {
        @JsonProperty("transaction")
        private List<Transaction> transactions;

        public List<Transaction> getTransactions() {
            return transactions != null ? transactions : List.of();
        }

        @Getter
        public static class Transaction {
            private Long id;
            @JsonProperty("userid")
            private Long clientId;
            private String currency;
            private String gateway;
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime date;
            private String description;
            @JsonProperty("amountin")
            private BigDecimal amountIn;
            private BigDecimal fees;
            @JsonProperty("amountout")
            private BigDecimal amountOut;
            private BigDecimal rate;
            @JsonProperty("transid")
            private String transId;
            @JsonProperty("invoiceid")
            private Long invoiceId;
            @JsonProperty("refundid")
            private Long refundId;
        }
    }
}