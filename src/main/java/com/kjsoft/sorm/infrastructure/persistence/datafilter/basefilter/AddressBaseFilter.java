package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringExactCondition;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpFullTextCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//TODO: подумать как объеденить toPredicate в единую логику в рамках всех BaseFilter
@AllArgsConstructor
public class AddressBaseFilter {
    private StringRegExpFullTextCondition address;
    private StringExactCondition zip;
    private StringExactCondition country;
    private StringExactCondition region;
    private StringExactCondition city;

    private List<AddressBaseFilter> and;
    private List<AddressBaseFilter> or;
    private AddressBaseFilter not;

    public BooleanExpression toPredicate(StringPath addressColumn, StringPath zipColumn, StringPath countryColumn,
                                         StringPath regionColumn, StringPath cityColumn) {

        var conditions = new ArrayList<BooleanExpression>();

        if (address != null) {
            conditions.add(address.toPredicate(addressColumn));
        }

        if (zip != null) {
            conditions.add(zip.toPredicate(zipColumn));
        }

        if (country != null) {
            conditions.add(country.toPredicate(countryColumn));
        }

        if (region != null) {
            conditions.add(region.toPredicate(regionColumn));
        }

        if (city != null) {
            conditions.add(city.toPredicate(cityColumn));
        }

        if (and != null && !and.isEmpty()) {
            var andConditions = and.stream()
                    .map(filter -> filter.toPredicate(addressColumn, zipColumn, countryColumn,
                            regionColumn, cityColumn)
                    )
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (andConditions.length > 0) {
                conditions.add(Expressions.allOf(andConditions));
            }
        }

        if (or != null && !or.isEmpty()) {
            var orConditions = or.stream()
                    .map(filter -> filter.toPredicate(addressColumn, zipColumn, countryColumn,
                            regionColumn, cityColumn)
                    )
                    .filter(Objects::nonNull)
                    .toArray(BooleanExpression[]::new);

            if (orConditions.length > 0) {
                conditions.add(Expressions.anyOf(orConditions));
            }
        }

        if (not != null) {
            var notPredicate = not.toPredicate(addressColumn, zipColumn, countryColumn, regionColumn, cityColumn);
            if (notPredicate != null) {
                conditions.add(notPredicate.not());
            }
        }

        return Expressions.allOf(conditions.stream()
                .filter(Objects::nonNull)
                .toArray(BooleanExpression[]::new));
    }
}