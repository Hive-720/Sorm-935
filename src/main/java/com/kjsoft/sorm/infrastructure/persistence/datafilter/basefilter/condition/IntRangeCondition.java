package com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.operation.NumberOperations;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;


public record IntRangeCondition(Integer min, Integer max) {

    public BooleanExpression toPredicate(NumberPath<Integer> column) {
       return NumberOperations.between(column, min, max);
    }
}