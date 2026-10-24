package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.presentation.graphqlapi.error.BadRequestException;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeRange;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeRangeCondition;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTimeRangePresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.dateTimeRangeMapper.map(null));
    }

    @Test
    void map_fillsMinAndMaxNormalizedToUtc() {
        DateTimeRangeCondition result = mappers.dateTimeRangeMapper.map(
                new DateTimeRange("2026-01-02T03:04:05+03:00", "2026-01-03T00:00:00Z"));

        assertEquals(OffsetDateTime.parse("2026-01-02T00:04:05Z"), result.getMin());
        assertEquals(OffsetDateTime.parse("2026-01-03T00:00:00Z"), result.getMax());
    }

    @Test
    void map_partialValues_keepNulls() {
        DateTimeRangeCondition minOnly = mappers.dateTimeRangeMapper.map(new DateTimeRange("2026-01-02T00:00:00Z", null));
        assertEquals(OffsetDateTime.parse("2026-01-02T00:00:00Z"), minOnly.getMin());
        assertNull(minOnly.getMax());

        DateTimeRangeCondition maxOnly = mappers.dateTimeRangeMapper.map(new DateTimeRange(null, "2026-01-03T00:00:00Z"));
        assertNull(maxOnly.getMin());
        assertEquals(OffsetDateTime.parse("2026-01-03T00:00:00Z"), maxOnly.getMax());
    }

    @Test
    void map_unparseableMin_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> mappers.dateTimeRangeMapper.map(new DateTimeRange("bad", "2026-01-03T00:00:00Z")));
    }

    @Test
    void map_unparseableMax_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> mappers.dateTimeRangeMapper.map(new DateTimeRange("2026-01-02T00:00:00Z", "bad")));
    }
}
