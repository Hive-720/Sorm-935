package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientGroupFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientGroupFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.clientGroupMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        ClientGroupFilterInput nestedAnd = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ClientGroupFilterInput nestedOr = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ClientGroupFilterInput nestedNot = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        ClientGroupFilterInput input = new ClientGroupFilterInput(
                SampleFilters.dateTime(SampleFilters.D02),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        ClientGroupFilter result = mappers.clientGroupMapper.map(input);

        assertNotNull(result.getUpdatedAt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getUpdatedAt().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getUpdatedAt().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getUpdatedAt().getUtc().getEq());
    }
}
