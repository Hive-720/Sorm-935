package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.OnlyNumberDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.TrimDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
public class GetOrdersResponse extends Response {
    private Orders orders;

    public List<Orders.Order> getOrders() {
        return orders != null && orders.order != null ? orders.order : List.of();
    }


    @Getter
    public static class Orders {
        private List<Order> order;


        @Getter
        public static class Order {
            private Long id;
            @JsonProperty("ordernum")
            private Long orderNum;
            @JsonProperty("userid")
            private Long clientId;
            @JsonProperty("contactid")
            private Long contactId;
            @JsonProperty("requestor_id")
            private Long requestorId;
            @JsonProperty("admin_requestor_id")
            private Long adminRequestorId;
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime date;
            @JsonProperty("nameservers")
            private String nameServers;
            @JsonProperty("transfersecret")
            private String transferSecret;
            private String renewals;
            @JsonProperty("promocode")
            private String promoCode;
            @JsonProperty("promotype")
            private String promoType;
            @JsonProperty("promovalue")
            private String promoValue;
            @JsonProperty("orderdata")
            private String orderData;
            private BigDecimal amount;
            @JsonProperty("paymentmethod")
            private String paymentMethod;
            @JsonProperty("invoiceid")
            private Long invoiceId;
            private String status;
            @JsonProperty("ipaddress")
            private String ipAddress;
            @JsonProperty("fraudmodule")
            private String fraudModule;
            @JsonProperty("fraudoutput")
            private String fraudOutput;
            private String notes;
            @JsonProperty("paymentmethodname")
            private String paymentMethodName;
            @JsonProperty("paymentstatus")
            private String paymentStatus;
            private String name;
            @JsonProperty("currencyprefix")
            @JsonDeserialize(using = TrimDeserializer.class)
            private String currencyPrefix;
            @JsonProperty("currencysuffix")
            @JsonDeserialize(using = TrimDeserializer.class)
            private String currencySuffix;
            @JsonProperty("frauddata")
            private String fraudData;
            @JsonProperty("validationdata")
            private String validationData;
            @JsonProperty("lineitems")
            private LineItems lineItems;


            public List<LineItems.LineItem> getLineItems() {
                return lineItems != null && lineItems.lineItem != null ? lineItems.lineItem : List.of();
            }

            @Getter
            public static class LineItems {
                @JsonProperty("lineitem")
                private List<LineItem> lineItem;


                @Getter
                public static class LineItem {
                    private String type;
                    @JsonProperty("relid")
                    private Long relId;
                    @JsonProperty("producttype")
                    private String productType;
                    private String product;
                    private String domain;
                    @JsonProperty("billingcycle")
                    @JsonDeserialize(using = OnlyNumberDeserializer.class)
                    private BigDecimal billingCycle;
                    private String amount;
                    private String status;
                }
            }
        }
    }
}