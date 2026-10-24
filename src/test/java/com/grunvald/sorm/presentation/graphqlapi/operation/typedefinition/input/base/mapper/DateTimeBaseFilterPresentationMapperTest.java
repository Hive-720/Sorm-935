package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.DateTimeBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeStringFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateTimeBaseFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.dateTimeBaseMapper.map(null));
    }

    @Test
    void map_fillsUtcAndAndOrNot() {
        DateTimeBaseFilter nested = new DateTimeBaseFilter(
                new DateTimeStringFilter(SampleFilters.D10, null, null, null, null, null, null),
                null, null, null);
        DateTimeBaseFilter input = new DateTimeBaseFilter(
                SampleFilters.dateTimeString(SampleFilters.D01),
                List.of(nested), List.of(nested), nested);

        com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter result = mappers.dateTimeBaseMapper.map(input);

        assertNotNull(result.getUtc());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), result.getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), result.getUtc().getIn().get(0));
        assertEquals(OffsetDateTime.parse(SampleFilters.D04), result.getUtc().getIn().get(1));
        assertEquals(OffsetDateTime.parse(SampleFilters.D05), result.getUtc().getLe());
        assertEquals(OffsetDateTime.parse(SampleFilters.D06), result.getUtc().getLt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D07), result.getUtc().getGe());
        assertEquals(OffsetDateTime.parse(SampleFilters.D08), result.getUtc().getGt());
        assertEquals(OffsetDateTime.parse(SampleFilters.D09), result.getUtc().getBetween().getMin());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getUtc().getBetween().getMax());

        assertEquals(1, result.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getAnd().get(0).getUtc().getEq());
        assertEquals(1, result.getOr().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getOr().get(0).getUtc().getEq());
        assertNotNull(result.getNot());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), result.getNot().getUtc().getEq());
    }

    @Test
    void map_nullFields_keepNulls() {
        com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter result =
                mappers.dateTimeBaseMapper.map(new DateTimeBaseFilter(null, null, null, null));

        assertNull(result.getUtc());
        assertNull(result.getAnd());
        assertNull(result.getOr());
        assertNull(result.getNot());
    }
}
