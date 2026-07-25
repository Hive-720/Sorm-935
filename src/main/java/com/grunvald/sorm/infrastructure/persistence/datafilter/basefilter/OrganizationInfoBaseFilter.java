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
public class OrganizationInfoBaseFilter {
    private StringRegExpExactCondition nameFull;

    private List<OrganizationInfoBaseFilter> and;
    private List<OrganizationInfoBaseFilter> or;
    private OrganizationInfoBaseFilter not;


    public BooleanExpression toPredicate(StringPath companyNameColumn) {

        var conditions = new ArrayList<BooleanExpression>();

        if (nameFull != null) {
            conditions.add(nameFull.toPredicate(companyNameColumn));
        }

        if (and != null && !and.isEmpty()) {
            var andConditions = and.stream()
                    .map(filter -> filter.toPredicate(companyNameColumn))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (andConditions.length > 0) {
                conditions.add(Expressions.allOf(andConditions));
            }
        }

        if (or != null && !or.isEmpty()) {
            var orConditions = or.stream()
                    .map(filter -> filter.toPredicate(companyNameColumn))
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (orConditions.length > 0) {
                conditions.add(Expressions.anyOf(orConditions));
            }
        }

        if (not != null) {
            var notPredicate = not.toPredicate(companyNameColumn);
            if (notPredicate != null) {
                conditions.add(notPredicate.not());
            }
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}