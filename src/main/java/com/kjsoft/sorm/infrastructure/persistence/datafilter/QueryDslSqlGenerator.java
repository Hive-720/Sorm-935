package com.kjsoft.sorm.infrastructure.persistence.datafilter;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.querydsl.core.JoinType.DEFAULT;


@Component
@RequiredArgsConstructor
public class QueryDslSqlGenerator {
    private final Configuration postgresSqlSerializerConfig;


    @SuppressWarnings({"rawtypes", "unchecked"})
    public Pair<String, List<Object>> generateSelect(@NotNull EntityFilter filter, String cursor, Integer offset,
                                                     @NonNull Integer limit) {
        var qEntity = filter.getQEntity();

        var joins = new ArrayDeque<Join>();
        var predicate = filter.toPredicate(joins);
        joins.add(new Join(DEFAULT, null, qEntity));

        var metadata = new DefaultQueryMetadata();
        metadata.setProjection(qEntity.getFieldsForSelectSql());
        metadata.addWhere(predicate);
        addAllJoin(metadata, joins);
        addPagination(cursor, offset, limit, metadata, qEntity);

        var serializer = new SQLSerializerForDatabaseClient(postgresSqlSerializerConfig);
        serializer.serialize(metadata, false);

        var sql = serializer.toString();
        var params = serializer.getConstants();

        return Pair.of(sql, params);
    }

    private void addPagination(String cursor, Integer offset,
                               Integer limit, QueryMetadata metadata, QEntity qEntity) {
        if (cursor == null && offset != null) {
            metadata.setOffset(offset.longValue());
        } else if (cursor != null) {
            metadata.addWhere(qEntity.getId().gt(Long.parseLong(cursor)));
        }

        metadata.addOrderBy(qEntity.getId().asc());
        metadata.setLimit((long) limit);
    }

    private void addAllJoin(QueryMetadata metadata, Deque<Join> joins) {
        for (var join : joins) {
            metadata.addJoin(join.type, join.targetEntity.getRelationalPathBase());
            if (join.sourceId != null) {
                var condition = join.sourceId.eq(join.targetId);
                metadata.addJoinCondition(condition);
            }
        }
    }


    public static class Join {
        private final JoinType type;
        private final NumberPath<Long> sourceId;
        private final NumberPath<Long> targetId;
        private final QEntity targetEntity;

        public Join(JoinType type, NumberPath<Long> sourceId, QEntity targetEntity) {
            this.type = type;
            this.sourceId = sourceId;
            this.targetId = targetEntity.getId();
            this.targetEntity = targetEntity;
        }

        public Join(JoinType type, NumberPath<Long> sourceId, NumberPath<Long> targetId, QEntity targetEntity) {
            this.type = type;
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.targetEntity = targetEntity;
        }
    }
}