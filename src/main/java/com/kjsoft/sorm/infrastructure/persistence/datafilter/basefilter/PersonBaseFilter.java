package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@AllArgsConstructor
public class PersonBaseFilter {
    private StringTermFullTextCondition fullName;
    private StringTermExactCondition firstName;
    private StringTermExactCondition lastName;

    private List<PersonBaseFilter> and;
    private List<PersonBaseFilter> or;
    private PersonBaseFilter not;


    public BooleanExpression toPredicate(StringPath columnFullName, StringPath columnFirstName,
                                         StringPath columnLastName) {

        var conditions = new ArrayList<BooleanExpression>();

        if (fullName != null) {
            conditions.add(fullName.toPredicate(columnFullName));
        }

        if (firstName != null) {
            conditions.add(firstName.toPredicate(columnFirstName));
        }

        if (lastName != null) {
            conditions.add(lastName.toPredicate(columnLastName));
        }

        if (and != null && !and.isEmpty()) {
            var andConditions = and.stream()
                    .map(filter -> filter.toPredicate(columnFullName, columnFirstName, columnLastName))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (andConditions.length > 0) {
                conditions.add(Expressions.allOf(andConditions));
            }
        }

        if (or != null && !or.isEmpty()) {
            var orConditions = or.stream()
                    .map(filter -> filter.toPredicate(columnFullName, columnFirstName, columnLastName))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (orConditions.length > 0) {
                conditions.add(Expressions.anyOf(orConditions));
            }
        }

        if (not != null) {
            var notPredicate = not.toPredicate(columnFullName, columnFirstName, columnLastName);
            if (notPredicate != null) {
                conditions.add(notPredicate.not());
            }
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}