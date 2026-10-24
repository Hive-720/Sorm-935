package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import com.grunvald.sorm.service.whmcsprovider.dto.PaymentMethodProviderDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodMapperTest {

    private final PaymentMethodMapper mapper = new PaymentMethodMapperImpl();

    @Test
    void map_fullDto_mapsAllFields() {
        var dto = new PaymentMethodProviderDto();
        dto.setModule("paypal");
        dto.setDisplayName("PayPal");

        PaymentMethod result = mapper.map(dto);

        assertEquals("paypal", result.getModule());
        assertEquals("PayPal", result.getDisplayName());
        assertNull(result.getId());
        assertNull(result.getUpdatedAt());
        assertFalse(result.isDeleted());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
