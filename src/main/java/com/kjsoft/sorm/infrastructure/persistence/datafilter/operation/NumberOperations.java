package com.kjsoft.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.List;


public final class NumberOperations {

    public static BooleanExpression eq(NumberPath<Integer> column, Integer value) {
        return value != null ? column.eq(value) : null;
    }

    public static BooleanExpression in(NumberPath<Integer> column, List<Integer> in) {
        return in != null && !in.isEmpty() ? column.in(in) : null;
    }

    public static BooleanExpression le(NumberPath<Integer> column, Integer value) {
        return value != null ? column.loe(value) : null;
    }

    public static BooleanExpression lt(NumberPath<Integer> column, Integer value) {
        return value != null ? column.lt(value) : null;
    }

    public static BooleanExpression ge(NumberPath<Integer> column, Integer value) {
        return value != null ? column.goe(value) : null;
    }

    public static BooleanExpression gt(NumberPath<Integer> column, Integer value) {
        return value != null ? column.gt(value) : null;
    }

    public static BooleanExpression between(NumberPath<Integer> column, Integer min, Integer max) {
        return min == null || max == null ? null : column.between(min, max);
    }
}