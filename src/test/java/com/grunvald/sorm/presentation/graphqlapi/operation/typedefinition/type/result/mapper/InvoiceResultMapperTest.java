package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.InvoiceResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.invoiceResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceResultMapperTest {

    private InvoiceResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = invoiceResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        first.setId(1L);
        first.setTotal(new BigDecimal("100.00"));
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice();
        second.setId(2L);
        second.setTotal(new BigDecimal("200.00"));

        InvoiceResult result = mapper.map(new InvoiceSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(1L, result.result().get(0).id());
        assertEquals(new BigDecimal("100.00"), result.result().get(0).total());
        assertEquals(2L, result.result().get(1).id());
        assertEquals(new BigDecimal("200.00"), result.result().get(1).total());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
