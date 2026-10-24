package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonBaseFilterTest {

    private static final StringPath FULL = Expressions.stringPath("full");
    private static final StringPath FIRST = Expressions.stringPath("first");
    private static final StringPath LAST = Expressions.stringPath("last");

    @Test
    void allNulls_returnsNull() {
        assertNull(new PersonBaseFilter(null, null, null, null, null, null).toPredicate(FULL, FIRST, LAST));
    }

    @Test
    void allFields_bindEachConditionToItsColumn() {
        var predicate = new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, "john doe", null),
                new StringTermExactCondition(null, null, "Alice", null),
                new StringTermExactCondition(null, null, "Smith", List.of("Smirnov", "Sidorov")),
                null, null, null
        ).toPredicate(FULL, FIRST, LAST);

        var sql = predicate.toString();
        assertTrue(sql.contains("to_tsvector('simple', full)"));
        assertTrue(sql.contains("john"));
        assertTrue(sql.contains("ILIKE ALL"));
        assertTrue(sql.contains("%john%"));
        assertTrue(sql.contains("%doe%"));
        assertTrue(sql.contains("first = Alice"));
        assertTrue(sql.contains("last = Smith"));
        assertTrue(sql.contains("last in [Smirnov, Sidorov]"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new PersonBaseFilter(
                null, null, null,
                List.of(fullNameFilter("a"), fullNameFilter("b")),
                List.of(fullNameFilter("c"), fullNameFilter("d")),
                fullNameFilter("e")
        ).toPredicate(FULL, FIRST, LAST);

        var sql = predicate.toString();
        assertTrue(sql.contains("&&"));
        assertTrue(sql.contains("||"));
        assertTrue(sql.contains("!to_tsvector('simple', full) @@ to_tsquery('simple', e)"));
    }

    private static PersonBaseFilter fullNameFilter(String name) {
        return new PersonBaseFilter(
                new StringTermFullTextCondition(List.of(name), null, null, null),
                null, null, null, null, null
        );
    }
}
