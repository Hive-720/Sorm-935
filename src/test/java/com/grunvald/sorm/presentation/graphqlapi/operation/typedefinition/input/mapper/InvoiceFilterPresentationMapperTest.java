package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.InvoiceFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.InvoiceFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvoiceFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.invoiceMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        InvoiceFilterInput nestedAnd = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null, null, null, null, null, null, null);
        InvoiceFilterInput nestedOr = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null, null, null, null, null, null, null);
        InvoiceFilterInput nestedNot = new InvoiceFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null, null, null, null, null, null, null);
        InvoiceFilterInput input = new InvoiceFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                SampleFilters.dateTime(SampleFilters.D04),
                SampleFilters.dateTime(SampleFilters.D05),
                SampleFilters.dateTime(SampleFilters.D06),
                SampleFilters.dateTime(SampleFilters.D07),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        InvoiceFilter result = mappers.invoiceMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getDueDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), result.getDatePaid().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D04), result.getDateRefunded().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D05), result.getDateCancelled().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D06), result.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D07), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
