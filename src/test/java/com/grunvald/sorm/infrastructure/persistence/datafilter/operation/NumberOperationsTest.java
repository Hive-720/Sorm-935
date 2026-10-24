package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NumberOperationsTest {

    private static final NumberPath<Integer> COLUMN = Expressions.numberPath(Integer.class, "col");

    @Test
    void eq_null_returnsNull() {
        assertNull(NumberOperations.eq(COLUMN, null));
    }

    @Test
    void eq_value_returnsPredicate() {
        assertTrue(NumberOperations.eq(COLUMN, 5).toString().contains("col = 5"));
    }

    @Test
    void le_lt_ge_gt_returnPredicates() {
        assertTrue(NumberOperations.le(COLUMN, 5).toString().contains("col <= 5"));
        assertTrue(NumberOperations.lt(COLUMN, 5).toString().contains("col < 5"));
        assertTrue(NumberOperations.ge(COLUMN, 5).toString().contains("col >= 5"));
        assertTrue(NumberOperations.gt(COLUMN, 5).toString().contains("col > 5"));
    }

    @Test
    void between_nullBound_returnsNull() {
        assertNull(NumberOperations.between(COLUMN, null, 5));
        assertNull(NumberOperations.between(COLUMN, 1, null));
    }

    @Test
    void between_bounds_returnsPredicate() {
        assertTrue(NumberOperations.between(COLUMN, 1, 5).toString().contains("col between 1 and 5"));
    }

    @Test
    void in_nullOrEmpty_returnsNull() {
        assertNull(NumberOperations.in(COLUMN, null));
        assertNull(NumberOperations.in(COLUMN, List.of()));
    }

    @Test
    void in_values_returnsPredicate() {
        assertTrue(NumberOperations.in(COLUMN, List.of(1, 2)).toString().contains("col in [1, 2]"));
    }
}
