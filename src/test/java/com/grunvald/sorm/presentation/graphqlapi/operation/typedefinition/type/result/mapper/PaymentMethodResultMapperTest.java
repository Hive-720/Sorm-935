package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.PaymentMethodResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.paymentMethodResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentMethodResultMapperTest {

    private PaymentMethodResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = paymentMethodResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod();
        first.setId(1L);
        first.setModule("paypal");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod();
        second.setId(2L);
        second.setModule("stripe");

        PaymentMethodResult result = mapper.map(new PaymentMethodSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("paypal", result.result().get(0).moduleName());
        assertEquals("stripe", result.result().get(1).moduleName());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
