package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.grunvald.sorm.infrastructure.persistence.datafilter.operation.ArrayOperations;
import com.grunvald.sorm.infrastructure.persistence.datafilter.operation.StringOperations;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record StringRegExpExactCondition(String regexp, String eq, List<String> in) {

    public BooleanExpression toPredicate(StringPath column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(StringOperations.regex(column, regexp));
        conditions.add(StringOperations.eq(column, eq));
        conditions.add(StringOperations.in(column, in));

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }

    public BooleanExpression toPredicate(ArrayPath<String, String> column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(ArrayOperations.regex(column, regexp));
        conditions.add(ArrayOperations.eq(column, eq));
        conditions.add(ArrayOperations.in(column, in));

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}