package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayOperationsTest {

    private static final ArrayPath<String, String> COLUMN = Expressions.arrayPath(String.class, "col");

    @Test
    void eq_blankOrNull_returnsNull() {
        assertNull(ArrayOperations.eq(COLUMN, null));
        assertNull(ArrayOperations.eq(COLUMN, "  "));
    }

    @Test
    void eq_value_rendersValueEqualsAnyColumn() {
        var predicate = ArrayOperations.eq(COLUMN, "value");

        assertTrue(predicate != null);
        var sql = predicate.toString();
        assertTrue(sql.contains("value = ANY (col)"), sql);
    }

    @Test
    void anyLike_blank_returnsNull() {
        assertNull(ArrayOperations.anyLike(COLUMN, null));
        assertNull(ArrayOperations.anyLike(COLUMN, ""));
    }

    @Test
    void anyLike_pattern_rendersLikeAny() {
        var predicate = ArrayOperations.anyLike(COLUMN, "%.ru");

        assertTrue(predicate != null);
        var sql = predicate.toString();
        assertTrue(sql.contains("%.ru LIKE ANY (col)"), sql);
    }

    @Test
    void anyNotLike_blank_returnsNull() {
        assertNull(ArrayOperations.anyNotLike(COLUMN, null));
        assertNull(ArrayOperations.anyNotLike(COLUMN, " "));
    }

    @Test
    void anyNotLike_pattern_rendersNotLikeAll() {
        var predicate = ArrayOperations.anyNotLike(COLUMN, "%:%");

        assertTrue(predicate != null);
        var sql = predicate.toString();
        assertTrue(sql.contains("NOT (%:% LIKE ALL (col))"), sql);
    }

    @Test
    void in_nullOrEmpty_returnsNull() {
        assertNull(ArrayOperations.in(COLUMN, null));
        assertNull(ArrayOperations.in(COLUMN, List.of()));
    }

    @Test
    void in_values_rendersOverlap() {
        var predicate = ArrayOperations.in(COLUMN, List.of("a", "b"));

        assertTrue(predicate != null);
        assertTrue(predicate.toString().contains("&&"));
    }

    @Test
    void regex_blank_returnsNull() {
        assertNull(ArrayOperations.regex(COLUMN, null));
        assertNull(ArrayOperations.regex(COLUMN, ""));
    }

    @Test
    void regex_value_rendersRegexAny() {
        var predicate = ArrayOperations.regex(COLUMN, ".*@grunvald.*");

        assertTrue(predicate != null);
        var sql = predicate.toString();
        assertTrue(sql.contains(".*@grunvald.* ~ ANY (col)"), sql);
    }
}
