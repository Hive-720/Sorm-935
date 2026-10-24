package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InnBaseFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new InnBaseFilter(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void inn_appliesTermsAndExact() {
        var predicate = new InnBaseFilter(
                new StringTermExactCondition(
                        List.of("7707", "7710"), List.of("7708"), "7711223344", List.of("7711", "7712")
                ),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("to_tsvector"));
        assertTrue(sql.contains("7707 & 7710"));
        assertTrue(sql.contains("7708"));
        assertTrue(sql.contains("col = 7711223344"));
        assertTrue(sql.contains("col in [7711, 7712]"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new InnBaseFilter(
                null,
                List.of(innFilter("1"), innFilter("2")),
                List.of(innFilter("3"), innFilter("4")),
                innFilter("5")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = 1 && col = 2"));
        assertTrue(sql.contains("col = 3 || col = 4"));
        assertTrue(sql.contains("!(col = 5)"));
    }

    private static InnBaseFilter innFilter(String inn) {
        return new InnBaseFilter(new StringTermExactCondition(null, null, inn, null), null, null, null);
    }
}
