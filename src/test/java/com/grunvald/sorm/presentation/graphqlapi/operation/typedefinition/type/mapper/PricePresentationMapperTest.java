package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.grunvald.sorm.testutil.MapStructTestFactory.pricePresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PricePresentationMapperTest {

    private PricePresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = pricePresentationMapper();
    }

    @Test
    void shouldMapAllFieldsIncludingSetupFees() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.product.model.Price();
        source.setCurrency("RUB");
        source.setMSetupFee(new BigDecimal("1.00"));
        source.setQSetupFee(new BigDecimal("2.00"));
        source.setSSetupFee(new BigDecimal("3.00"));
        source.setASetupFee(new BigDecimal("4.00"));
        source.setBSetupFee(new BigDecimal("5.00"));
        source.setTSetupFee(new BigDecimal("6.00"));
        source.setMonthly(new BigDecimal("7.00"));
        source.setQuarterly(new BigDecimal("8.00"));
        source.setSemiannually(new BigDecimal("9.00"));
        source.setAnnually(new BigDecimal("10.00"));
        source.setBiennially(new BigDecimal("11.00"));
        source.setTriennially(new BigDecimal("12.00"));

        Price result = mapper.map(source);

        assertEquals("RUB", result.currency());
        assertEquals(new BigDecimal("1.00"), result.mSetupFee());
        assertEquals(new BigDecimal("2.00"), result.qSetupFee());
        assertEquals(new BigDecimal("3.00"), result.sSetupFee());
        assertEquals(new BigDecimal("4.00"), result.aSetupFee());
        assertEquals(new BigDecimal("5.00"), result.bSetupFee());
        assertEquals(new BigDecimal("6.00"), result.tSetupFee());
        assertEquals(new BigDecimal("7.00"), result.monthly());
        assertEquals(new BigDecimal("8.00"), result.quarterly());
        assertEquals(new BigDecimal("9.00"), result.semiannually());
        assertEquals(new BigDecimal("10.00"), result.annually());
        assertEquals(new BigDecimal("11.00"), result.biennially());
        assertEquals(new BigDecimal("12.00"), result.triennially());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
