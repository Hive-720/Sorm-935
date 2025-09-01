package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.operation.DateOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public record DateTimeCondition(OffsetDateTime eq,
                                List<OffsetDateTime> in,
                                OffsetDateTime le,
                                OffsetDateTime lt,
                                OffsetDateTime ge,
                                OffsetDateTime gt,
                                DateTimeRangeCondition between
) {

    public BooleanExpression toPredicate(DateTimePath<OffsetDateTime> column) {
        var conditions = new ArrayList<BooleanExpression>();
        conditions.add(DateOperations.eq(column, eq));
        conditions.add(DateOperations.in(column, in));
        conditions.add(DateOperations.le(column, le));
        conditions.add(DateOperations.lt(column, lt));
        conditions.add(DateOperations.ge(column, ge));
        conditions.add(DateOperations.gt(column, gt));
        if (between != null) {
            conditions.add(between.toPredicate(column));
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}