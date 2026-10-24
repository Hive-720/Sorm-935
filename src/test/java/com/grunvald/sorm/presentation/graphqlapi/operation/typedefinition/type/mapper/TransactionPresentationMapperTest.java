package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Transaction;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.transactionPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private TransactionPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = transactionPresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction();
        source.setId(1L);
        source.setInvoiceId(2L);
        source.setClientId(3L);
        source.setTransId("TX-1");
        source.setRefundId(4L);
        source.setCurrency("RUB");
        source.setGateway("paypal");
        source.setDescription("payment");
        source.setAmountIn(new BigDecimal("100.00"));
        source.setAmountOut(new BigDecimal("0.00"));
        source.setFees(new BigDecimal("1.00"));
        source.setRate(new BigDecimal("90.00"));
        source.setDate(DATE);

        Transaction result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.invoiceId());
        assertEquals(3L, result.clientId());
        assertEquals("TX-1", result.transId());
        assertEquals(4L, result.refundId());
        assertEquals("RUB", result.currency());
        assertEquals("paypal", result.gateway());
        assertEquals("payment", result.description());
        assertEquals(new BigDecimal("100.00"), result.amountIn());
        assertEquals(new BigDecimal("0.00"), result.amountOut());
        assertEquals(new BigDecimal("1.00"), result.fees());
        assertEquals(new BigDecimal("90.00"), result.rate());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
