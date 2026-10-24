package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StringOperationsTest {

    private static final StringPath COLUMN = Expressions.stringPath("col");

    @Test
    void eq_blankOrNull_returnsNull() {
        assertNull(StringOperations.eq(COLUMN, null));
        assertNull(StringOperations.eq(COLUMN, ""));
        assertNull(StringOperations.eq(COLUMN, "   "));
    }

    @Test
    void eq_value_returnsEqualityPredicate() {
        var predicate = StringOperations.eq(COLUMN, "value");

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("col = value"));
    }

    @Test
    void regex_blankOrNull_returnsNull() {
        assertNull(StringOperations.regex(COLUMN, null));
        assertNull(StringOperations.regex(COLUMN, " "));
    }

    @Test
    void regex_value_returnsRegexTemplate() {
        var predicate = StringOperations.regex(COLUMN, "re.*");

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("~"));
        assertTrue(predicate.toString().contains("re.*"));
    }

    @Test
    void in_nullOrEmpty_returnsNull() {
        assertNull(StringOperations.in(COLUMN, null));
        assertNull(StringOperations.in(COLUMN, List.of()));
    }

    @Test
    void in_values_returnsInPredicate() {
        var predicate = StringOperations.in(COLUMN, List.of("a", "b"));

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("a"));
        assertTrue(predicate.toString().contains("b"));
    }

    @Test
    void gt_null_returnsNull() {
        assertNull(StringOperations.gt(COLUMN, null));
    }

    @Test
    void between_nullBound_returnsNull() {
        assertNull(StringOperations.between(COLUMN, null, "b"));
        assertNull(StringOperations.between(COLUMN, "a", null));
    }

    @Test
    void allOfTerms_nullOrEmpty_returnsNull() {
        assertNull(StringOperations.allOfTerms(COLUMN, null));
        assertNull(StringOperations.allOfTerms(COLUMN, List.of()));
    }

    @Test
    void allOfTerms_joinsTermsWithAmpersandAndSkipsBlank() {
        var predicate = StringOperations.allOfTerms(COLUMN, Arrays.asList(" a ", null, "", "b"));

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("to_tsvector"));
        assertTrue(predicate.toString().contains("a & b"));
    }

    @Test
    void anyOfTerms_joinsTermsWithPipe() {
        var predicate = StringOperations.anyOfTerms(COLUMN, List.of("a", "b"));

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("a | b"));
    }

    @Test
    void allOfText_blankOrNull_returnsNull() {
        assertNull(StringOperations.allOfText(COLUMN, null));
        assertNull(StringOperations.allOfText(COLUMN, ""));
        assertNull(StringOperations.allOfText(COLUMN, "!@#"));
    }

    @Test
    void allOfText_tokenizesAndWrapsTokens() {
        var predicate = StringOperations.allOfText(COLUMN, "Foo,bar!baz");

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("ILIKE ALL"));
        assertTrue(predicate.toString().contains("%foo%"));
        assertTrue(predicate.toString().contains("%bar%"));
        assertTrue(predicate.toString().contains("%baz%"));
    }

    @Test
    void anyOfText_usesIlikeAny() {
        var predicate = StringOperations.anyOfText(COLUMN, "foo bar");

        assertNotNull(predicate);
        assertTrue(predicate.toString().contains("ILIKE ANY"));
        assertTrue(predicate.toString().contains("%foo%"));
        assertTrue(predicate.toString().contains("%bar%"));
    }
}
