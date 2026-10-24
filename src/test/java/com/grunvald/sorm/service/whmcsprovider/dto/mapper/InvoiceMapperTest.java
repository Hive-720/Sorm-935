package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceAdditionalDataProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.InvoiceProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceMapperTest {

    private final InvoiceMapper mapper = new InvoiceMapperImpl();

    private static InvoiceProviderDto fullDto() {
        var dto = new InvoiceProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setDueDate(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        dto.setDatePaid(OffsetDateTime.parse("2026-03-02T03:04:05Z"));
        dto.setDateRefunded(OffsetDateTime.parse("2026-05-02T03:04:05Z"));
        dto.setDateCancelled(OffsetDateTime.parse("2026-06-02T03:04:05Z"));
        dto.setSubTotal(new BigDecimal("10.0"));
        dto.setCredit(new BigDecimal("1.0"));
        dto.setTax(new BigDecimal("2.0"));
        dto.setTax2(new BigDecimal("0.5"));
        dto.setTotal(new BigDecimal("11.5"));
        dto.setTaxRate(new BigDecimal("20.0"));
        dto.setTaxRate2(new BigDecimal("5.0"));
        dto.setStatus("Paid");
        dto.setPaymentMethod("paypal");
        dto.setCreatedAt(OffsetDateTime.parse("2026-07-02T03:04:05Z"));
        dto.setUpdatedAt(OffsetDateTime.parse("2026-08-02T03:04:05Z"));
        dto.setCurrencyCode("USD");
        return dto;
    }

    private static List<InvoiceAdditionalDataProviderDto.Item> items() {
        var item1 = new InvoiceAdditionalDataProviderDto.Item();
        item1.setId(1L);
        item1.setRelId(3L);
        item1.setType("Hosting");
        item1.setDescription("desc1");
        item1.setAmount(new BigDecimal("10.0"));
        item1.setTaxed(true);

        var item2 = new InvoiceAdditionalDataProviderDto.Item();
        item2.setId(2L);
        item2.setRelId(4L);
        item2.setType("Domain");
        item2.setDescription("desc2");
        item2.setAmount(new BigDecimal("1.5"));
        item2.setTaxed(false);

        return List.of(item1, item2);
    }

    @Test
    void map_fullDto_mapsAllFieldsAndPassesBalanceAndItems() {
        Invoice result = mapper.map(fullDto(), new BigDecimal("9.5"), items());

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getDueDate());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getDatePaid());
        assertEquals(OffsetDateTime.parse("2026-05-02T03:04:05Z"), result.getDateRefunded());
        assertEquals(OffsetDateTime.parse("2026-06-02T03:04:05Z"), result.getDateCancelled());
        assertEquals(new BigDecimal("10.0"), result.getSubTotal());
        assertEquals(new BigDecimal("1.0"), result.getCredit());
        assertEquals(new BigDecimal("2.0"), result.getTax());
        assertEquals(new BigDecimal("0.5"), result.getTax2());
        assertEquals(new BigDecimal("11.5"), result.getTotal());
        assertEquals(new BigDecimal("20.0"), result.getTaxRate());
        assertEquals(new BigDecimal("5.0"), result.getTaxRate2());
        assertEquals("Paid", result.getStatus());
        assertEquals("USD", result.getCurrency());
        assertEquals("paypal", result.getPaymentMethod());
        assertEquals(OffsetDateTime.parse("2026-07-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-08-02T03:04:05Z"), result.getUpdatedAt());

        assertEquals(new BigDecimal("9.5"), result.getBalance());

        assertEquals(2, result.getItems().size());
        var invoiceItem1 = result.getItems().get(0);
        assertEquals(1L, invoiceItem1.getId());
        assertEquals(3L, invoiceItem1.getRelId());
        assertEquals("Hosting", invoiceItem1.getType());
        assertEquals("desc1", invoiceItem1.getDescription());
        assertEquals(new BigDecimal("10.0"), invoiceItem1.getAmount());
        assertEquals(Boolean.TRUE, invoiceItem1.getTaxed());

        var invoiceItem2 = result.getItems().get(1);
        assertEquals(2L, invoiceItem2.getId());
        assertEquals(4L, invoiceItem2.getRelId());
        assertEquals("Domain", invoiceItem2.getType());
        assertEquals("desc2", invoiceItem2.getDescription());
        assertEquals(new BigDecimal("1.5"), invoiceItem2.getAmount());
        assertEquals(Boolean.FALSE, invoiceItem2.getTaxed());
    }

    @Test
    void map_allNull_returnsNull() {
        assertNull(mapper.map(null, null, null));
    }

    @Test
    void map_nullDto_passesBalanceAndItems() {
        Invoice result = mapper.map(null, new BigDecimal("9.5"), items());

        assertNotNull(result);
        assertEquals(new BigDecimal("9.5"), result.getBalance());
        assertEquals(2, result.getItems().size());
        assertNull(result.getId());
        assertNull(result.getCurrency());
    }
}
