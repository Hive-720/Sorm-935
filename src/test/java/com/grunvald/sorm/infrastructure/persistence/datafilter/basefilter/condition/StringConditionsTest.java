package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringConditionsTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");
    private static final ArrayPath<String, String> ARRAY_COLUMN = Expressions.arrayPath(String.class, "col");

    @Test
    void exactCondition_allNulls_returnsNull() {
        assertNull(new StringExactCondition(null, null).toPredicate(COLUMN));
    }

    @Test
    void exactCondition_eqAndIn_combinedWithAnd() {
        var sql = new StringExactCondition("a", List.of("b", "c")).toPredicate(COLUMN).toString();
        assertTrue(sql.contains("col = a"));
        assertTrue(sql.contains("col in [b, c]"));
        assertTrue(sql.contains("&&"));
    }

    @Test
    void regexpExactCondition_stringColumn_usesStringOperations() {
        var sql = new StringRegExpExactCondition("^a.*", "b", List.of("c")).toPredicate(COLUMN).toString();
        assertTrue(sql.contains("~"));
        assertTrue(sql.contains("col = b"));
        assertTrue(sql.contains("col = c"));
    }

    @Test
    void regexpExactCondition_arrayColumn_usesArrayOperations() {
        var sql = new StringRegExpExactCondition("^a.*", "b", List.of("c")).toPredicate(ARRAY_COLUMN).toString();
        assertTrue(sql.contains("^a.* ~ ANY (col)"));
        assertTrue(sql.contains("b = ANY (col)"));
        assertTrue(sql.contains("&&"));
    }

    @Test
    void regexpExactCondition_allNulls_returnsNull() {
        assertNull(new StringRegExpExactCondition(null, null, null).toPredicate(COLUMN));
        assertNull(new StringRegExpExactCondition(null, null, null).toPredicate(ARRAY_COLUMN));
    }

    @Test
    void regexpFullTextCondition_allNulls_returnsNull() {
        assertNull(new StringRegExpFullTextCondition(null, null, null).toPredicate(COLUMN));
    }

    @Test
    void regexpFullTextCondition_combinesRegexAndText() {
        var sql = new StringRegExpFullTextCondition("^a.*", "foo bar", "baz").toPredicate(COLUMN).toString();
        assertTrue(sql.contains("^a.*"));
        assertTrue(sql.contains("ILIKE ALL"));
        assertTrue(sql.contains("%foo%"));
        assertTrue(sql.contains("ILIKE ANY"));
        assertTrue(sql.contains("%baz%"));
    }

    @Test
    void termExactCondition_allNulls_returnsNull() {
        assertNull(new StringTermExactCondition(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void termExactCondition_combinesTermsAndExact() {
        var sql = new StringTermExactCondition(List.of("a", "b"), List.of("c"), "d", List.of("e"))
                .toPredicate(COLUMN)
                .toString();
        assertTrue(sql.contains("a & b"));
        assertTrue(sql.contains("c"));
        assertTrue(sql.contains("col = d"));
        assertTrue(sql.contains("col = e"));
    }

    @Test
    void termFullTextCondition_allNulls_returnsNull() {
        assertNull(new StringTermFullTextCondition(null, null, null, null).toPredicate(COLUMN));
    }

    @Test
    void termFullTextCondition_combinesTermsAndText() {
        var sql = new StringTermFullTextCondition(List.of("a"), List.of("b"), "c d", "e")
                .toPredicate(COLUMN)
                .toString();
        assertTrue(sql.contains("to_tsvector"));
        assertTrue(sql.contains("ILIKE ALL"));
        assertTrue(sql.contains("%c%"));
        assertTrue(sql.contains("ILIKE ANY"));
        assertTrue(sql.contains("%e%"));
    }
}
