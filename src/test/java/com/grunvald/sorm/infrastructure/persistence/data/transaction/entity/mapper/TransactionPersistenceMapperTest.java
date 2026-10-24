package com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.TransactionDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.transactionPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private TransactionPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = transactionPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        TransactionDb db = new TransactionDb();
        db.setId(1L);
        db.setInvoiceId(2L);
        db.setClientId(3L);
        db.setTransId("TX-1");
        db.setRefundId(4L);
        db.setCurrency("RUB");
        db.setGateway("paypal");
        db.setDescription("payment");
        db.setAmountIn(new BigDecimal("100.00"));
        db.setAmountOut(new BigDecimal("0.00"));
        db.setFees(new BigDecimal("1.00"));
        db.setRate(new BigDecimal("90.00"));
        db.setDate(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getInvoiceId());
        assertEquals(3L, result.getClientId());
        assertEquals("TX-1", result.getTransId());
        assertEquals(4L, result.getRefundId());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getGateway());
        assertEquals("payment", result.getDescription());
        assertEquals(new BigDecimal("100.00"), result.getAmountIn());
        assertEquals(new BigDecimal("0.00"), result.getAmountOut());
        assertEquals(new BigDecimal("1.00"), result.getFees());
        assertEquals(new BigDecimal("90.00"), result.getRate());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction();
        model.setId(1L);
        model.setInvoiceId(2L);
        model.setClientId(3L);
        model.setTransId("TX-1");
        model.setRefundId(4L);
        model.setCurrency("RUB");
        model.setGateway("paypal");
        model.setDescription("payment");
        model.setAmountIn(new BigDecimal("100.00"));
        model.setAmountOut(new BigDecimal("0.00"));
        model.setFees(new BigDecimal("1.00"));
        model.setRate(new BigDecimal("90.00"));
        model.setDate(DATE);

        TransactionDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getInvoiceId());
        assertEquals(3L, result.getClientId());
        assertEquals("TX-1", result.getTransId());
        assertEquals(4L, result.getRefundId());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getGateway());
        assertEquals("payment", result.getDescription());
        assertEquals(new BigDecimal("100.00"), result.getAmountIn());
        assertEquals(new BigDecimal("0.00"), result.getAmountOut());
        assertEquals(new BigDecimal("1.00"), result.getFees());
        assertEquals(new BigDecimal("90.00"), result.getRate());
        assertEquals(DATE, result.getDate());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((TransactionDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction) null));
    }
}
