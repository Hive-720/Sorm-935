package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetPaymentMethodResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodProviderMapperTest {

    private final PaymentMethodProviderMapper mapper = new PaymentMethodProviderMapperImpl();

    @Test
    void map_fullPaymentMethod_mapsAllFields() {
        var source = new GetPaymentMethodResponse.PaymentMethod();
        source.setModule("paypal");
        source.setDisplayName("PayPal");

        PaymentMethodProviderDto result = mapper.map(source);

        assertEquals("paypal", result.getModule());
        assertEquals("PayPal", result.getDisplayName());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
