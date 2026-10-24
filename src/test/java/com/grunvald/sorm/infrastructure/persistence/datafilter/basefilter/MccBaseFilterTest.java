package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.IntCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MccBaseFilterTest {

    private static final NumberPath<Integer> COLUMN = Expressions.numberPath(Integer.class, "col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new MccBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void mcc_appliesIntConditions() {
        var predicate = new MccBaseFilter(
                new IntCondition(123, List.of(124, 125), null, null, null, null, null),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 123"));
        assertTrue(sql.contains("col in [124, 125]"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new MccBaseFilter(
                null,
                List.of(mccFilter(1), mccFilter(2)),
                List.of(mccFilter(3), mccFilter(4)),
                mccFilter(5)
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 1 && col = 2"));
        assertTrue(sql.contains("col = 3 || col = 4"));
        assertTrue(sql.contains("!(col = 5)"));
    }

    private static MccBaseFilter mccFilter(int mcc) {
        return new MccBaseFilter(new IntCondition(mcc, null, null, null, null, null, null), null, null, null);
    }
}
