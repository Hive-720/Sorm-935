package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.TransactionFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.transactionMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        TransactionFilterInput nestedAnd = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        TransactionFilterInput nestedOr = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        TransactionFilterInput nestedNot = new TransactionFilterInput(null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        TransactionFilterInput input = new TransactionFilterInput(
                SampleFilters.invoice(),
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        TransactionFilter result = mappers.transactionMapper.map(input);

        assertNotNull(result.getInvoice());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getInvoice().getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getInvoice().getDueDate().getUtc().getEq());

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());

        assertNotNull(result.getDate());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getDate().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
