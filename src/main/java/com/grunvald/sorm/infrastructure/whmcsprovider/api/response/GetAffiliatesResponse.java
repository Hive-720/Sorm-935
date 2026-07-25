package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.IntegerToBooleanDeserializer;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Setter
public class GetAffiliatesResponse extends Response {
    private Affiliates affiliates;

    public List<Affiliate> getAffiliates() {
        return affiliates != null && affiliates.affiliate != null ? affiliates.affiliate : List.of();
    }

    @Getter
    @Setter
    public static class Affiliates {
        private List<Affiliate> affiliate;
    }

    @Getter
    @Setter
    public static class Affiliate {
        private Long id;
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime date;
        @JsonProperty("clientid")
        private Long clientId;
        private Long visitors;
        @JsonProperty("paytype")
        private String payType;
        @JsonProperty("payamount")
        private BigDecimal payAmount;
        @JsonProperty("onetime")
        @JsonDeserialize(using = IntegerToBooleanDeserializer.class)
        private Boolean oneTime;
        private BigDecimal balance;
        private BigDecimal withdrawn;
        @JsonProperty("created_at")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime createdAt;
        @JsonProperty("updated_at")
        @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
        private OffsetDateTime updatedAt;
    }
}