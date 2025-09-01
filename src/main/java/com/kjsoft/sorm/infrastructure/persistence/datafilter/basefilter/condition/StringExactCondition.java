package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.operation.StringOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record StringExactCondition(String eq, List<String> in) {

    public BooleanExpression toPredicate(StringPath column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(StringOperations.eq(column, eq));
        conditions.add(StringOperations.in(column, in));

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}