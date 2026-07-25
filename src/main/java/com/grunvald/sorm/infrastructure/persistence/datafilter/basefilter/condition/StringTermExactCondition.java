package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.grunvald.sorm.infrastructure.persistence.datafilter.operation.StringOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record StringTermExactCondition(List<String> allOfTerms,
                                       List<String> anyOfTerms,
                                       String eq,
                                       List<String> in
) {

    public BooleanExpression toPredicate(StringPath column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(StringOperations.eq(column, eq));
        conditions.add(StringOperations.allOfTerms(column, allOfTerms));
        conditions.add(StringOperations.anyOfTerms(column, anyOfTerms));
        conditions.add(StringOperations.in(column, in));

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}