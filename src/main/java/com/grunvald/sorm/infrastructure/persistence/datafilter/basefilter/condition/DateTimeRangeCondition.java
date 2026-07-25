package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.grunvald.sorm.infrastructure.persistence.datafilter.operation.DateOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;

import java.time.OffsetDateTime;


public record DateTimeRangeCondition(OffsetDateTime min, OffsetDateTime max) {

    public BooleanExpression toPredicate(DateTimePath<OffsetDateTime> column) {
        return DateOperations.between(column, min, max);
    }
}