package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Invoice;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.InvoiceItem;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.invoicePresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoicePresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private InvoicePresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = invoicePresentationMapper();
    }

    @Test
    void shouldMapAllFields() {
        var item = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice.InvoiceItem();
        item.setId(11L);
        item.setRelId(12L);
        item.setType("Hosting");
        item.setDescription("VPS");
        item.setAmount(new BigDecimal("10.50"));
        item.setTaxed(true);

        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        source.setId(1L);
        source.setClientId(2L);
        source.setSubTotal(new BigDecimal("100.00"));
        source.setTotal(new BigDecimal("110.00"));
        source.setCredit(new BigDecimal("0.00"));
        source.setBalance(new BigDecimal("110.00"));
        source.setTax(new BigDecimal("10.00"));
        source.setTax2(new BigDecimal("0.00"));
        source.setTaxRate(new BigDecimal("10.00"));
        source.setTaxRate2(new BigDecimal("0.00"));
        source.setStatus("Unpaid");
        source.setCurrency("RUB");
        source.setPaymentMethod("paypal");
        source.setItems(List.of(item));
        source.setDate(DATE);
        source.setDueDate(DATE);
        source.setDatePaid(DATE);
        source.setDateRefunded(DATE);
        source.setDateCancelled(DATE);
        source.setCreatedAt(DATE);
        source.setUpdatedAt(DATE);

        Invoice result = mapper.map(source);

        assertEquals(1L, result.id());
        assertEquals(2L, result.clientId());
        assertEquals(new BigDecimal("100.00"), result.subTotal());
        assertEquals(new BigDecimal("0.00"), result.credit());
        assertEquals(new BigDecimal("110.00"), result.total());
        assertEquals(new BigDecimal("110.00"), result.balance());
        assertEquals(new BigDecimal("10.00"), result.tax());
        assertEquals(new BigDecimal("0.00"), result.tax2());
        assertEquals(new BigDecimal("10.00"), result.taxRate());
        assertEquals(new BigDecimal("0.00"), result.taxRate2());
        assertEquals("Unpaid", result.status());
        assertEquals("paypal", result.paymentMethod());
        assertEquals("RUB", result.currency());
        assertEquals(1, result.items().size());
        InvoiceItem mappedItem = result.items().getFirst();
        assertEquals("Hosting", mappedItem.type());
        assertEquals("12", mappedItem.relId());
        assertEquals("VPS", mappedItem.description());
        assertEquals("10.50", mappedItem.amount());
        assertEquals(true, mappedItem.taxed());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.date());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.dueDate());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.datePaid());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.dateRefunded());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.dateCancelled());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.createdAt());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
