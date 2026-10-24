package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.mapper;

import com.grunvald.sorm.presentation.graphqlapi.error.BadRequestException;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeRange;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.DateTimeStringFilter;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.service.whmcsdata.filter.condition.DateTimeCondition;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateTimeStringFilterPresentationMapperTest {

    private final FilterMappers mappers = new FilterMappers();

    @Test
    void map_null_returnsNull() {
        assertNull(mappers.dateTimeStringFilterMapper.map(null));
    }

    @Test
    void map_fillsAllFields() {
        DateTimeStringFilter input = new DateTimeStringFilter(
                "2026-01-02T03:04:05+03:00",
                List.of("2026-01-03T00:00:00Z", "2026-01-04T00:00:00Z"),
                "2026-01-05T00:00:00Z",
                "2026-01-06T00:00:00Z",
                "2026-01-07T00:00:00Z",
                "2026-01-08T00:00:00Z",
                new DateTimeRange("2026-01-09T00:00:00Z", "2026-01-10T00:00:00Z"));

        DateTimeCondition result = mappers.dateTimeStringFilterMapper.map(input);

        assertEquals(OffsetDateTime.parse("2026-01-02T00:04:05Z"), result.getEq());
        assertEquals(List.of(
                        OffsetDateTime.parse("2026-01-03T00:00:00Z"),
                        OffsetDateTime.parse("2026-01-04T00:00:00Z")),
                result.getIn());
        assertEquals(OffsetDateTime.parse("2026-01-05T00:00:00Z"), result.getLe());
        assertEquals(OffsetDateTime.parse("2026-01-06T00:00:00Z"), result.getLt());
        assertEquals(OffsetDateTime.parse("2026-01-07T00:00:00Z"), result.getGe());
        assertEquals(OffsetDateTime.parse("2026-01-08T00:00:00Z"), result.getGt());
        assertNotNull(result.getBetween());
        assertEquals(OffsetDateTime.parse("2026-01-09T00:00:00Z"), result.getBetween().getMin());
        assertEquals(OffsetDateTime.parse("2026-01-10T00:00:00Z"), result.getBetween().getMax());
    }

    @Test
    void map_nullFields_keepNulls() {
        DateTimeCondition result = mappers.dateTimeStringFilterMapper.map(
                new DateTimeStringFilter(null, null, null, null, null, null, null));

        assertNull(result.getEq());
        assertNull(result.getIn());
        assertNull(result.getLe());
        assertNull(result.getLt());
        assertNull(result.getGe());
        assertNull(result.getGt());
        assertNull(result.getBetween());
    }

    @Test
    void map_unparseableEq_throwsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> mappers.dateTimeStringFilterMapper.map(
                        new DateTimeStringFilter("bad", null, null, null, null, null, null)));
    }
}
