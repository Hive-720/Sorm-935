package com.grunvald.sorm.infrastructure.persistence.datafilter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.RelationalPathBase;


public interface QEntity {

    SimpleExpression<Tuple> getFieldsForSelectSql();

    RelationalPathBase<?> getRelationalPathBase();

    NumberPath<Long> getId();
}