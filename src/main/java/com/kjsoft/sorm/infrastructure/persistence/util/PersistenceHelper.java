package com.kjsoft.sorm.infrastructure.persistence.util;

import io.r2dbc.spi.Statement;
import org.springframework.r2dbc.core.DatabaseClient;


public final class PersistenceHelper {

    private PersistenceHelper() {
    }

    public static boolean hasNextPage(Integer offset, Integer limit, long totalCount) {
        return (offset + limit) < totalCount;
    }

    public static void bindOrNull(Statement statement, int index, Object value, Class<?> type) {
        if (value != null) {
            statement.bind(index, value);
        } else {
            statement.bindNull(index, type);
        }
    }

    public static DatabaseClient.GenericExecuteSpec bindOrNull(DatabaseClient.GenericExecuteSpec spec, int index, Object value, Class<?> type) {
        if (value != null) {
            return spec.bind(index, value);
        } else {
            return spec.bindNull(index, type);
        }
    }
}