package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailBaseFilterTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");
    private static final ArrayPath<String, String> ARRAY_COLUMN = Expressions.arrayPath(String.class, "col");

    @Test
    void allNulls_returnsNull() {
        assertNull(new EmailBaseFilter(null, null, null, null).toPredicate(COLUMN));
        assertNull(new EmailBaseFilter(null, null, null, null).toPredicate(ARRAY_COLUMN));
    }

    @Test
    void stringColumn_usesStringOperations() {
        var predicate = new EmailBaseFilter(
                new StringRegExpExactCondition("^a.*@", "a@b.com", List.of("c@d.com", "e@f.com")),
                null, null, null
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col ~ ^a.*@"));
        assertTrue(sql.contains("col = a@b.com"));
        assertTrue(sql.contains("col in [c@d.com, e@f.com]"));
    }

    @Test
    void arrayColumn_usesArrayOperations() {
        var predicate = new EmailBaseFilter(
                new StringRegExpExactCondition("^a.*@", "a@b.com", List.of("c@d.com")),
                null, null, null
        ).toPredicate(ARRAY_COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("^a.*@ ~ ANY (col)"));
        assertTrue(sql.contains("a@b.com = ANY (col)"));
        assertTrue(sql.contains("&&"));
    }

    @Test
    void and_or_not_combineNestedFilters() {
        var predicate = new EmailBaseFilter(
                null,
                List.of(emailFilter("a@b.com"), emailFilter("c@d.com")),
                List.of(emailFilter("e@f.com"), emailFilter("g@h.com")),
                emailFilter("i@j.com")
        ).toPredicate(COLUMN);

        var sql = predicate.toString();
        assertTrue(sql.contains("col = a@b.com && col = c@d.com"));
        assertTrue(sql.contains("col = e@f.com || col = g@h.com"));
        assertTrue(sql.contains("!(col = i@j.com)"));
    }

    private static EmailBaseFilter emailFilter(String email) {
        return new EmailBaseFilter(new StringRegExpExactCondition(null, email, null), null, null, null);
    }
}
