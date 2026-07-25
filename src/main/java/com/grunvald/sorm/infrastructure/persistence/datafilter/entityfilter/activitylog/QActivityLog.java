package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog;

import com.grunvald.sorm.infrastructure.persistence.data.activitylog.entity.ActivityLogDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QActivityLog extends RelationalPathBase<ActivityLogDb> implements QEntity {

    public static final QActivityLog ACTIVITY_LOG = new QActivityLog("aLog");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> userId = createNumber("user_id", Long.class);
    private final StringPath description = createString("description");
    private final StringPath username = createString("username");
    private final StringPath ipAddress = createString("ip_address");
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            userId,
            description,
            username,
            ipAddress,
            date
    );

    public QActivityLog(String variable) {
        super(ActivityLogDb.class, forVariable(variable), "public", "activity_logs");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<ActivityLogDb> getRelationalPathBase() {
        return ACTIVITY_LOG;
    }
}
