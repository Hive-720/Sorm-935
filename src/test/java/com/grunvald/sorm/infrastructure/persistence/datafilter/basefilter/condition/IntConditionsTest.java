package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntConditionsTest {

    private static final NumberPath<Integer> COLUMN = Expressions.numberPath(Integer.class, "col");

    @Test
    void condition_allNulls_returnsNull() {
        assertNull(new IntCondition(null, null, null, null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void condition_allFields_setCombinesWithAnd() {
        var predicate = new IntCondition(
                1, List.of(2, 3), 4, 5, 6, 7, new IntRangeCondition(8, 9)
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 1"));
        assertTrue(sql.contains("col in [2, 3]"));
        assertTrue(sql.contains("col <= 4"));
        assertTrue(sql.contains("col < 5"));
        assertTrue(sql.contains("col >= 6"));
        assertTrue(sql.contains("col > 7"));
        assertTrue(sql.contains("col between 8 and 9"));
    }

    @Test
    void condition_partialFields_skipsNullOperations() {
        var predicate = new IntCondition(1, null, null, null, null, null, null).toPredicate(COLUMN);

        assertTrue(predicate.toString().contains("col = 1"));
    }

    @Test
    void rangeCondition_nullBound_returnsNull() {
        assertNull(new IntRangeCondition(null, 9).toPredicate(COLUMN));
        assertNull(new IntRangeCondition(8, null).toPredicate(COLUMN));
    }
}
