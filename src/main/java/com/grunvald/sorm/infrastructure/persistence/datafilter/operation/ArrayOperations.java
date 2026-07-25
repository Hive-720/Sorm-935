package com.grunvald.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.apache.logging.log4j.util.Strings;

import java.util.List;


public class ArrayOperations {

    private static final String ARRAY_ANY_PATTERN = "{1} ~ ANY ({0})";
    private static final String ARRAY_LIKE_ANY_PATTERN = "{1} LIKE ANY ({0})";
    private static final String ARRAY_EQUALS_ANY_PATTERN = "{1} = ANY ({0})";
    private static final String ARRAY_IN_PATTERN = "{0} && {1}";
    private static final String NOT_ALL_LIKE_PATTERN = "NOT ({1} LIKE ALL ({0}))";


    public static BooleanExpression eq(ArrayPath<String, String> column, String value) {
        if (Strings.isBlank(value)) {
            return null;
        }

        return Expressions.booleanTemplate(ARRAY_EQUALS_ANY_PATTERN, Expressions.constant(value), column);
    }

    public static BooleanExpression anyLike(ArrayPath<String, String> arrayColumn, String pattern) {
        if (Strings.isBlank(pattern)) {
            return null;
        }

        return Expressions.booleanTemplate(
                ARRAY_LIKE_ANY_PATTERN,
                arrayColumn,
                Expressions.constant(pattern)
        );
    }

    public static BooleanExpression anyNotLike(ArrayPath<String, String> arrayColumn, String pattern) {
        if (Strings.isBlank(pattern)) {
            return null;
        }

        return Expressions.booleanTemplate(
                NOT_ALL_LIKE_PATTERN,
                arrayColumn,
                Expressions.constant(pattern)
        );
    }

    public static BooleanExpression in(ArrayPath<String, String> column, List<String> in) {
        if (in == null || in.isEmpty()) {
            return null;
        }

        return Expressions.booleanTemplate(
                ARRAY_IN_PATTERN,
                column,
                Expressions.constant(in.toArray(new String[0]))
        );
    }

    public static BooleanExpression regex(ArrayPath<String, String> column, String regex) {
        if (Strings.isBlank(regex)) {
            return null;
        }

        return Expressions.booleanTemplate(ARRAY_ANY_PATTERN, column, regex);
    }
}