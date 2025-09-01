package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.operation.NumberOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record IntCondition(Integer eq,
                           List<Integer> in,
                           Integer le,
                           Integer lt,
                           Integer ge,
                           Integer gt,
                           IntRangeCondition between
) {

    public BooleanExpression toPredicate(NumberPath<Integer> column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(NumberOperations.eq(column, eq));
        conditions.add(NumberOperations.in(column, in));
        conditions.add(NumberOperations.le(column, le));
        conditions.add(NumberOperations.lt(column, lt));
        conditions.add(NumberOperations.ge(column, ge));
        conditions.add(NumberOperations.gt(column, gt));
        if (between != null) {
            conditions.add(between.toPredicate(column));
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}