package com.kjsoft.sorm.infrastructure.persistence.datafilter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
public abstract class EntityFilter<FILTER extends EntityFilter<FILTER>> {
    protected List<FILTER> and;
    protected List<FILTER> or;
    protected FILTER not;

    public abstract QEntity getQEntity();

    protected abstract void processPredicate(Deque<QueryDslSqlGenerator.Join> joins, List<BooleanExpression> conditions);

    public BooleanExpression toPredicate(Deque<QueryDslSqlGenerator.Join> joins) {

        var conditions = new ArrayList<BooleanExpression>();

        processPredicate(joins, conditions);

        if (and != null && !and.isEmpty()) {
            var andConditions = and.stream()
                    .map(userFilter -> userFilter.toPredicate(joins))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (andConditions.length > 0) {
                conditions.add(Expressions.allOf(andConditions));
            }
        }

        if (or != null && !or.isEmpty()) {
            var orConditions = or.stream()
                    .map(userFilter -> userFilter.toPredicate(joins))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (orConditions.length > 0) {
                conditions.add(Expressions.anyOf(orConditions));
            }
        }

        if (not != null) {
            var notPredicate = not.toPredicate(joins);
            if (notPredicate != null) {
                conditions.add(notPredicate.not());
            }
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}