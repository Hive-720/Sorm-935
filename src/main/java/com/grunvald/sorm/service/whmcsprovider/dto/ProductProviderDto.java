package com.grunvald.sorm.service.whmcsprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Builder
public class ProductProviderDto {
    private Long id;
    private String type;
    private String name;
    private String description;
    private String payType;
    private List<Price> prices;
    private List<Config> configs;


    @Getter
    @Setter
    public static class Config {
        private Long id;
        private String name;
        private String type;
        private List<Option> options;

        @Getter
        @Setter
        public static class Option {
            private int id;
            private String name;
            private List<Price> prices;
        }
    }

    @Getter
    @Setter
    public static class Price {
        private String currency;
        private BigDecimal mSetupFee;
        private BigDecimal qSetupFee;
        private BigDecimal sSetupFee;
        private BigDecimal aSetupFee;
        private BigDecimal bSetupFee;
        private BigDecimal tSetupFee;
        private BigDecimal monthly;
        private BigDecimal quarterly;
        private BigDecimal semiannually;
        private BigDecimal annually;
        private BigDecimal biennially;
        private BigDecimal triennially;
    }
}