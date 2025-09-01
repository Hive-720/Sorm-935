package com.kjsoft.sorm.infrastructure.persistence.datafilter.operation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;

import java.time.OffsetDateTime;
import java.util.List;


public final class DateOperations {

    public static BooleanExpression eq(DateTimePath<OffsetDateTime> column, OffsetDateTime value) {
        return value != null ? column.eq(value) : null;
    }

    public static BooleanExpression le(DateTimePath<OffsetDateTime> column, OffsetDateTime value) {
        return value != null ? column.loe(value) : null;
    }

    public static BooleanExpression lt(DateTimePath<OffsetDateTime> column, OffsetDateTime value) {
        return value != null ? column.lt(value) : null;
    }

    public static BooleanExpression ge(DateTimePath<OffsetDateTime> column, OffsetDateTime value) {
        return value != null ? column.goe(value) : null;
    }

    public static BooleanExpression gt(DateTimePath<OffsetDateTime> column, OffsetDateTime value) {
        return value != null ? column.gt(value) : null;
    }

    public static BooleanExpression between(DateTimePath<OffsetDateTime> column, OffsetDateTime min, OffsetDateTime max) {
        return min == null || max == null ? null : column.between(min, max);
    }

    public static BooleanExpression in(DateTimePath<OffsetDateTime> column, List<OffsetDateTime> in) {
        return in != null && !in.isEmpty() ? column.in(in) : null;
    }
}
