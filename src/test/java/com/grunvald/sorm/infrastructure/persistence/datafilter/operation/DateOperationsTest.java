package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateOperationsTest {

    private static final DateTimePath<OffsetDateTime> COLUMN = Expressions.dateTimePath(OffsetDateTime.class, "col");
    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void eq_null_returnsNull() {
        assertNull(DateOperations.eq(COLUMN, null));
    }

    @Test
    void eq_value_returnsPredicate() {
        assertTrue(DateOperations.eq(COLUMN, DATE).toString().contains("col = 2026-01-02T03:04:05Z"));
    }

    @Test
    void le_and_ge_returnPredicates() {
        assertTrue(DateOperations.le(COLUMN, DATE).toString().contains("col <= 2026-01-02T03:04:05Z"));
        assertTrue(DateOperations.ge(COLUMN, DATE).toString().contains("col >= 2026-01-02T03:04:05Z"));
    }

    @Test
    void lt_and_gt_returnPredicates() {
        assertTrue(DateOperations.lt(COLUMN, DATE).toString().contains("col < 2026-01-02T03:04:05Z"));
        assertTrue(DateOperations.gt(COLUMN, DATE).toString().contains("col > 2026-01-02T03:04:05Z"));
    }

    @Test
    void between_nullBound_returnsNull() {
        assertNull(DateOperations.between(COLUMN, null, DATE));
        assertNull(DateOperations.between(COLUMN, DATE, null));
    }

    @Test
    void between_bounds_returnsPredicate() {
        var max = DATE.plusDays(1);
        var sql = DateOperations.between(COLUMN, DATE, max).toString();
        assertTrue(sql.contains("col between 2026-01-02T03:04:05Z and 2026-01-03T03:04:05Z"));
    }

    @Test
    void in_nullOrEmpty_returnsNull() {
        assertNull(DateOperations.in(COLUMN, null));
        assertNull(DateOperations.in(COLUMN, List.of()));
    }

    @Test
    void in_values_returnsPredicate() {
        assertTrue(DateOperations.in(COLUMN, List.of(DATE)).toString().contains("2026-01-02T03:04:05Z"));
    }
}
