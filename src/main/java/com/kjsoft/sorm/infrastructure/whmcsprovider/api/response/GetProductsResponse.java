package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GetProductsResponse extends Response {
    private Products products;

    public List<Products.Product> getProducts() {
        return products != null && products.product != null ? products.product : List.of();
    }

    @Getter
    public static class Products {
        private List<Product> product;


        @Getter
        public static class Product {
            @JsonProperty("pid")
            private Long id;
            private Long gid;
            private String type;
            private String name;
            private String slug;
            @JsonProperty("product_url")
            private String productUrl;
            private String description;
            private String module;
            @JsonProperty("paytype")
            private String payType;
            private Map<String, Pricing> pricing;
            @JsonProperty("customfields")
            private CustomFields customFields;
            @JsonProperty("configoptions")
            private ProductConfig configs;


            public List<ProductConfig.Config> getConfigs() {
                return configs != null && configs.productConfigs != null ? configs.productConfigs : List.of();
            }

            public static class CustomFields {
                @JsonProperty("customfield")
                private List<CustomField> customField;

                public static class CustomField {
                    private int id;
                    private String name;
                    private String description;
                    private String required;
                }
            }

            @Getter
            public static class ProductConfig {
                @JsonProperty("configoption")
                private List<Config> productConfigs;

                @Getter
                public static class Config {
                    private Long id;
                    private String name;
                    private String type;
                    private Options options;


                    public List<Options.Option> getOptions() {
                        return options != null && options.options != null ? options.options : List.of();
                    }

                    @Getter
                    public static class Options {
                        @JsonProperty("option")
                        private List<Option> options;


                        @Getter
                        public static class Option {
                            private int id;
                            private String name;
                            private Object rawName;
                            private Object recurring;
                            private Object required;
                            private Map<String, Pricing> pricing;
                        }
                    }
                }
            }
        }
    }


    @Getter
    public static class Pricing {
        private String prefix;
        private String suffix;
        @JsonProperty("msetupfee")
        private BigDecimal mSetupFee;
        @JsonProperty("qsetupfee")
        private BigDecimal qSetupFee;
        @JsonProperty("ssetupfee")
        private BigDecimal sSetupFee;
        @JsonProperty("asetupfee")
        private BigDecimal aSetupFee;
        @JsonProperty("bsetupfee")
        private BigDecimal bSetupFee;
        @JsonProperty("tsetupfee")
        private BigDecimal tSetupFee;
        private BigDecimal monthly;
        private BigDecimal quarterly;
        private BigDecimal semiannually;
        private BigDecimal annually;
        private BigDecimal biennially;
        private BigDecimal triennially;
    }
}