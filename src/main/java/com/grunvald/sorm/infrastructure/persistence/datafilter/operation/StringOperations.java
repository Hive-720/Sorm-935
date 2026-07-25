package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.*;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public final class StringOperations {

    private static final String TS_QUERY_PATTERN = "to_tsvector('simple', {0}) @@ to_tsquery('simple', {1})";
    private static final String REGEX_PATTERN = "({0} ~ {1})";
    private static final String ILIKE_ANY_PATTERN = "{0} ILIKE ANY ({1})";
    private static final String ILIKE_ALL_PATTERN = "{0} ILIKE ALL ({1})";


    public static BooleanExpression eq(StringPath column, String value) {
        return Strings.isNotBlank(value) ? column.eq(value) : null;
    }

    public static BooleanExpression regex(StringPath column, String regex) {
        if (Strings.isNotBlank(regex)) {
            return Expressions.booleanTemplate(REGEX_PATTERN, column, regex);
        }
        return null;
    }

    public static BooleanExpression in(StringPath column, List<String> in) {
        return in != null && !in.isEmpty() ? column.in(in) : null;
    }

    public static BooleanExpression gt(StringPath column, String value) {
        return value != null ? column.gt(value) : null;
    }

    public static BooleanExpression between(StringPath column, String min, String max) {
        return min == null || max == null ? null : column.between(min, max);
    }

    public static BooleanExpression allOfTerms(StringPath column, List<String> allOfTerms) {
        return tsQuery(column, allOfTerms, '&');
    }

    //TODO: возможна необходима токенизация как для any так и для all, если term будет содержать символы
    public static BooleanExpression anyOfTerms(StringPath column, List<String> anyOfTerms) {
        return tsQuery(column, anyOfTerms, '|');
    }

    public static BooleanExpression allOfText(StringPath column, String allOfText) {
        return ilikeQuery(ILIKE_ALL_PATTERN, column, allOfText);
    }

    public static BooleanExpression anyOfText(StringPath column, String anyOfText) {
        return ilikeQuery(ILIKE_ANY_PATTERN, column, anyOfText);
    }

    private static BooleanTemplate tsQuery(StringPath column, List<String> terms, char separator) {
        if (terms != null && !terms.isEmpty()) {
            return Expressions.booleanTemplate(TS_QUERY_PATTERN, column, getTsQueryString(terms, separator));
        }

        return null;
    }

    private static String getTsQueryString(List<String> terms, char separator) {
        return terms.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(term -> !term.isEmpty())
                .collect(Collectors.joining(" " + separator + " "));
    }

    private static BooleanExpression ilikeQuery(String template, StringPath column, String text) {
        if (Strings.isNotBlank(text)) {
            var tokens = tokenizeQuery(text);
            if (tokens.isEmpty()) {
                return null;
            }

            var patterns = tokens.stream()
                    .map(token -> "%" + token + "%")
                    .collect(Collectors.toList());

            return Expressions.booleanTemplate(template, column, Expressions.constant(patterns));
        }

        return null;
    }

    private static List<String> tokenizeQuery(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        var tokens = query.toLowerCase().split("[^\\p{L}\\p{N}]+");

        return Arrays.stream(tokens)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }
}