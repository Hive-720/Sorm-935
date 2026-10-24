package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.CreditFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.CreditFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.creditMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        CreditFilterInput nestedAnd = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null);
        CreditFilterInput nestedOr = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null);
        CreditFilterInput nestedNot = new CreditFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null);
        CreditFilterInput input = new CreditFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        CreditFilter result = mappers.creditMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getDate().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().getFirst().getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().getFirst().getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
