package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.IpAddressDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsSplitIpAddressByLineBreak;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
public class GetClientProductsResponse extends Response {
    private String result;
    @JsonProperty("clientid")
    private Long clientId;
    @JsonProperty("serviceid")
    private Long serviceId;
    private Long pid;
    private String domain;
    private Products products;

    public List<Products.Product> getClientProducts() {
        return products != null && products.product != null ? products.product : List.of();
    }

    @Getter
    public static class Products {
        private List<Product> product;


        @Getter
        public static class Product {
            private Long id;
            @JsonProperty("clientid")
            private Long clientId;
            @JsonProperty("orderid")
            private Long orderId;
            @JsonProperty("ordernumber")
            private Long orderNumber;
            private Long pid;
            @JsonProperty("regdate")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime regDate;
            private String name;
            @JsonProperty("translated_name")
            private String translatedName;
            @JsonProperty("groupname")
            private String groupName;
            @JsonProperty("translated_groupname")
            private String translatedGroupName;
            private String domain;
            @JsonProperty("dedicatedip")
            @JsonDeserialize(using = IpAddressDeserializer.class)
            private String dedicatedIp;
            @JsonProperty("serverid")
            private Long serverId;
            @JsonProperty("servername")
            private String serverName;
            @JsonProperty("serverip")
            @JsonDeserialize(using = IpAddressDeserializer.class)
            private String serverIp;
            @JsonProperty("serverhostname")
            private String serverHostName;
            @JsonProperty("suspensionreason")
            private String suspensionReason;
            @JsonProperty("firstpaymentamount")
            private BigDecimal firstPaymentAmount;
            @JsonProperty("recurringamount")
            private BigDecimal recurringAmount;
            @JsonProperty("paymentmethod")
            private String paymentMethod;
            @JsonProperty("paymentmethodname")
            private String paymentMethodName;
            @JsonProperty("billingcycle")
            private String billingCycle;
            @JsonProperty("nextduedate")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime nextDueDate;
            private String status;
            private String username;
            private String password;
            @JsonProperty("subscriptionid")
            private Long subscriptionId;
            @JsonProperty("promoid")
            private Long promoId;
            @JsonProperty("overideautosuspend")
            private Integer overrideAutoSuspend;
            @JsonProperty("overidesuspenduntil")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime overrideSuspendUntil;
            private String ns1;
            private String ns2;
            @JsonProperty("assignedips")
            @JsonDeserialize(using = WhmcsSplitIpAddressByLineBreak.class)
            private List<String> assignedIps;
            private String notes;
            @JsonProperty("diskusage")
            private Integer diskUsage;
            @JsonProperty("disklimit")
            private Integer diskLimit;
            @JsonProperty("bwusage")
            private Integer bwUsage;
            @JsonProperty("bwlimit")
            private Integer bwLimit;
            @JsonProperty("lastupdate")
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime lastUpdate;
            @JsonProperty("customfields")
            private CustomFields customFields;
            @JsonProperty("configoptions")
            private ConfigOptions configOptions;

            public List<ConfigOptions.ConfigOption> getConfigOptions() {
                return configOptions != null && configOptions.configOptions != null ? configOptions.configOptions : List.of();
            }


            @Getter
            public static class CustomFields {
                @JsonProperty("customfield")
                private List<CustomField> customField;

                @Getter
                public static class CustomField {
                    private Long id;
                    private String name;
                    @JsonProperty("translated_name")
                    private String translatedName;
                    private String value;
                }
            }


            @Getter
            public static class ConfigOptions {
                @JsonProperty("configoption")
                private List<ConfigOption> configOptions;

                @Getter
                public static class ConfigOption {
                    private Long id;
                    private String option;
                    private String type;
                    private String value;
                }
            }
        }
    }
}