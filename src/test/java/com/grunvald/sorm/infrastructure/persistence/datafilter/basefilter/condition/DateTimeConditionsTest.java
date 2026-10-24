package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeConditionsTest {

    private static final DateTimePath<OffsetDateTime> COLUMN = Expressions.dateTimePath(OffsetDateTime.class, "col");
    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void condition_allNulls_returnsNull() {
        assertNull(new DateTimeCondition(null, null, null, null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void condition_allFields_setCombinesWithAnd() {
        var predicate = new DateTimeCondition(
                DATE, List.of(DATE.plusDays(1)), DATE.plusDays(2), DATE.plusDays(3),
                DATE.plusDays(4), DATE.plusDays(5), new DateTimeRangeCondition(DATE.plusDays(6), DATE.plusDays(7))
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("col = 2026-01-03T03:04:05Z"));
        assertTrue(sql.contains("col <= 2026-01-04T03:04:05Z"));
        assertTrue(sql.contains("col < 2026-01-05T03:04:05Z"));
        assertTrue(sql.contains("col >= 2026-01-06T03:04:05Z"));
        assertTrue(sql.contains("col > 2026-01-07T03:04:05Z"));
        assertTrue(sql.contains("col between 2026-01-08T03:04:05Z and 2026-01-09T03:04:05Z"));
    }

    @Test
    void condition_betweenOnly_returnsBetweenPredicate() {
        var predicate = new DateTimeCondition(
                null, null, null, null, null, null, new DateTimeRangeCondition(DATE, DATE.plusDays(1))
        ).toPredicate(COLUMN);

        assertTrue(predicate.toString().contains("col between 2026-01-02T03:04:05Z and 2026-01-03T03:04:05Z"));
    }

    @Test
    void rangeCondition_nullBound_returnsNull() {
        assertNull(new DateTimeRangeCondition(null, DATE).toPredicate(COLUMN));
        assertNull(new DateTimeRangeCondition(DATE, null).toPredicate(COLUMN));
    }
}
