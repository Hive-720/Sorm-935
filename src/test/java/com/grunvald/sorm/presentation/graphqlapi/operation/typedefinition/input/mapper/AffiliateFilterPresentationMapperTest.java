package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.AffiliateFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliateFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.affiliateMapper.map(null));
    }

    @Test
    void map_fillsAllFieldsIncludingNestedAndOrNot() {
        AffiliateFilterInput nestedAnd = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null, null, null);
        AffiliateFilterInput nestedOr = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null, null, null);
        AffiliateFilterInput nestedNot = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null, null, null);
        AffiliateFilterInput input = new AffiliateFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);

        AffiliateFilter result = mappers.affiliateMapper.map(input);

        assertNotNull(result.getClient());
        assertEquals("exact-eq", result.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), result.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), result.getUpdatedAt().getUtc().getEq());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), result.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D12), result.getOr().get(0).getDate().getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D13), result.getNot().getDate().getUtc().getEq());
    }
}
