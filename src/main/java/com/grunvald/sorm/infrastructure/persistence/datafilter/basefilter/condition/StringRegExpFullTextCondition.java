package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.grunvald.sorm.infrastructure.persistence.datafilter.operation.StringOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.ArrayList;
import java.util.Objects;


public record StringRegExpFullTextCondition(String regexp, String allOfText, String anyOfText) {

    public BooleanExpression toPredicate(StringPath column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(StringOperations.regex(column, regexp));
        conditions.add(StringOperations.allOfText(column, allOfText));
        conditions.add(StringOperations.anyOfText(column, anyOfText));

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}