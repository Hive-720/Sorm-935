package com.grunvald.sorm.infrastructure.persistence.datafilter;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLSerializer;


public class SQLSerializerForDatabaseClient extends SQLSerializer {

    public SQLSerializerForDatabaseClient(Configuration conf) {
        super(conf);
    }

    @Override
    protected void serializeConstant(int parameterIndex, String constantLabel) {
        append("$" + parameterIndex);
    }
}