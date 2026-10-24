package com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.InvoiceDb;
import com.grunvald.sorm.service.exception.InternalErrorException;
import io.r2dbc.postgresql.codec.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.invoicePersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvoicePersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private InvoicePersistenceMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mapper = invoicePersistenceMapper();
        objectMapper = new ObjectMapper();
    }

    private List<com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice.InvoiceItem> items() {
        var item = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice.InvoiceItem();
        item.setId(11L);
        item.setRelId(12L);
        item.setType("Hosting");
        item.setDescription("VPS");
        item.setAmount(new BigDecimal("10.50"));
        item.setTaxed(true);
        return List.of(item);
    }

    @Test
    void shouldMapDbToModelWithJsonItems() throws Exception {
        InvoiceDb db = new InvoiceDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setSubTotal(new BigDecimal("100.00"));
        db.setTotal(new BigDecimal("110.00"));
        db.setCredit(new BigDecimal("0.00"));
        db.setBalance(new BigDecimal("110.00"));
        db.setTax(new BigDecimal("10.00"));
        db.setTax2(new BigDecimal("0.00"));
        db.setTaxRate(new BigDecimal("10.00"));
        db.setTaxRate2(new BigDecimal("0.00"));
        db.setStatus("Unpaid");
        db.setCurrency("RUB");
        db.setPaymentMethod("paypal");
        db.setItems(Json.of(objectMapper.writeValueAsString(items())));
        db.setDate(DATE);
        db.setDueDate(DATE);
        db.setDatePaid(DATE);
        db.setDateRefunded(DATE);
        db.setDateCancelled(DATE);
        db.setCreatedAt(DATE);
        db.setUpdatedAt(DATE);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(new BigDecimal("100.00"), result.getSubTotal());
        assertEquals(new BigDecimal("110.00"), result.getTotal());
        assertEquals(new BigDecimal("0.00"), result.getCredit());
        assertEquals(new BigDecimal("110.00"), result.getBalance());
        assertEquals(new BigDecimal("10.00"), result.getTax());
        assertEquals(new BigDecimal("0.00"), result.getTax2());
        assertEquals(new BigDecimal("10.00"), result.getTaxRate());
        assertEquals(new BigDecimal("0.00"), result.getTaxRate2());
        assertEquals("Unpaid", result.getStatus());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals(1, result.getItems().size());
        assertEquals(11L, result.getItems().getFirst().getId());
        assertEquals(12L, result.getItems().getFirst().getRelId());
        assertEquals("Hosting", result.getItems().getFirst().getType());
        assertEquals("VPS", result.getItems().getFirst().getDescription());
        assertEquals(new BigDecimal("10.50"), result.getItems().getFirst().getAmount());
        assertEquals(true, result.getItems().getFirst().getTaxed());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getDueDate());
        assertEquals(DATE, result.getDatePaid());
        assertEquals(DATE, result.getDateRefunded());
        assertEquals(DATE, result.getDateCancelled());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapModelToDbWithJsonItems() throws Exception {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        model.setId(1L);
        model.setClientId(2L);
        model.setSubTotal(new BigDecimal("100.00"));
        model.setTotal(new BigDecimal("110.00"));
        model.setCredit(new BigDecimal("0.00"));
        model.setBalance(new BigDecimal("110.00"));
        model.setTax(new BigDecimal("10.00"));
        model.setTax2(new BigDecimal("0.00"));
        model.setTaxRate(new BigDecimal("10.00"));
        model.setTaxRate2(new BigDecimal("0.00"));
        model.setStatus("Unpaid");
        model.setCurrency("RUB");
        model.setPaymentMethod("paypal");
        model.setItems(items());
        model.setDate(DATE);
        model.setDueDate(DATE);
        model.setDatePaid(DATE);
        model.setDateRefunded(DATE);
        model.setDateCancelled(DATE);
        model.setCreatedAt(DATE);
        model.setUpdatedAt(DATE);

        InvoiceDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(new BigDecimal("100.00"), result.getSubTotal());
        assertEquals(new BigDecimal("110.00"), result.getTotal());
        assertEquals(new BigDecimal("0.00"), result.getCredit());
        assertEquals(new BigDecimal("110.00"), result.getBalance());
        assertEquals(new BigDecimal("10.00"), result.getTax());
        assertEquals(new BigDecimal("0.00"), result.getTax2());
        assertEquals(new BigDecimal("10.00"), result.getTaxRate());
        assertEquals(new BigDecimal("0.00"), result.getTaxRate2());
        assertEquals("Unpaid", result.getStatus());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals(objectMapper.writeValueAsString(items()), result.getItems().asString());
        assertEquals(DATE, result.getDate());
        assertEquals(DATE, result.getDueDate());
        assertEquals(DATE, result.getDatePaid());
        assertEquals(DATE, result.getDateRefunded());
        assertEquals(DATE, result.getDateCancelled());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
    }

    @Test
    void shouldMapEmptyItemsToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        model.setItems(List.of());

        InvoiceDb result = mapper.map(model);

        assertNull(result.getItems());
    }

    @Test
    void shouldMapNullItemsToNullJson() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        model.setItems(null);

        InvoiceDb result = mapper.map(model);

        assertNull(result.getItems());
    }

    @Test
    void shouldMapNullJsonToNullItems() {
        InvoiceDb db = new InvoiceDb();
        db.setItems(null);

        var result = mapper.map(db);

        assertNull(result.getItems());
    }

    @Test
    void shouldThrowInternalErrorExceptionOnInvalidJson() {
        InvoiceDb db = new InvoiceDb();
        db.setItems(Json.of("невалидный json"));

        assertThrows(InternalErrorException.class, () -> mapper.map(db));
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((InvoiceDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice) null));
    }
}
