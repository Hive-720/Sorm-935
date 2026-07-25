package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer.WhmcsOffsetDateTimeDeserializer;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Getter
@Setter
public class GetCreditsResponse extends Response {
    @JsonProperty("clientid")
    private Long clientId;
    private Credits credits;

    public List<Credits.Credit> getCredits() {
        return credits != null && credits.credit != null ? credits.credit : List.of();
    }

    @Getter
    @Setter
    public static class Credits {
        private List<Credit> credit;

        @Getter
        @Setter
        public static class Credit {
            private Long id;
            @JsonDeserialize(using = WhmcsOffsetDateTimeDeserializer.class)
            private OffsetDateTime date;
            private String description;
            private BigDecimal amount;
            @JsonProperty("relid")
            private Long relId;
        }
    }
}