package com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
public class LoginFilter {
    private StringRegExpExactCondition login;

    private List<LoginFilter> and;
    private List<LoginFilter> or;
    private LoginFilter not;

    public BooleanExpression toPredicate(StringPath column) {

        var conditions = new ArrayList<BooleanExpression>();

        if (login != null) {
            conditions.add(login.toPredicate(column));
        }

        if (and != null && !and.isEmpty()) {
            var andConditions = and.stream()
                    .map(filter -> filter.toPredicate(column))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (andConditions.length > 0) {
                conditions.add(Expressions.allOf(andConditions));
            }
        }

        if (or != null && !or.isEmpty()) {
            var orConditions = or.stream()
                    .map(filter -> filter.toPredicate(column))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (orConditions.length > 0) {
                conditions.add(Expressions.anyOf(orConditions));
            }
        }

        if (not != null) {
            var notPredicate = not.toPredicate(column);
            if (notPredicate != null) {
                conditions.add(notPredicate.not());
            }
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}