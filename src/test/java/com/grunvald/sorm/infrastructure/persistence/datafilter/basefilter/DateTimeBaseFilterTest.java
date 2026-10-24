package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateTimeBaseFilterTest {

    private static final DateTimePath<OffsetDateTime> COLUMN = Expressions.dateTimePath(OffsetDateTime.class, "col");
    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        assertNull(new DateTimeBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void utcCondition_isApplied() {
        var predicate = new DateTimeBaseFilter(
                new DateTimeCondition(DATE, null, null, null, null, null, null), null, null, null
        ).toPredicate(COLUMN);

        assertTrue(predicate != null);
        assertTrue(predicate.toString().contains("col = 2026-01-02T03:04:05Z"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new DateTimeBaseFilter(
                null,
                List.of(utcFilter(1), utcFilter(2)),
                List.of(utcFilter(3), utcFilter(4)),
                utcFilter(5)
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 2026-01-03T03:04:05Z && col = 2026-01-04T03:04:05Z"));
        assertTrue(sql.contains("col = 2026-01-05T03:04:05Z || col = 2026-01-06T03:04:05Z"));
        assertTrue(sql.contains("!(col = 2026-01-07T03:04:05Z)"));
    }

    private static DateTimeBaseFilter utcFilter(int plusDays) {
        var date = DATE.plusDays(plusDays);
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
