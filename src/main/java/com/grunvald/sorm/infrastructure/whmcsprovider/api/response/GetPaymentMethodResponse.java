package com.grunvald.sorm.infrastructure.whmcsprovider.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GetPaymentMethodResponse extends Response {
    @JsonProperty("paymentmethods")
    private PaymentMethods paymentMethods;

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods.paymentMethod;
    }

    @Getter
    @Setter
    public static class PaymentMethods {
        @JsonProperty("paymentmethod")
        private List<PaymentMethod> paymentMethod;
    }

    @Getter
    @Setter
    public static class PaymentMethod {
        private String module;
        @JsonProperty("displayname")
        private String displayName;
    }
}