package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientLoginDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public final class QClientLogin extends RelationalPathBase<ClientLoginDb> implements QEntity {

    public static final QClientLogin CLIENT_LOGIN = new QClientLogin("clLog");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final StringPath ipAddress = createString("ip_address");
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            ipAddress,
            date
    );

    private QClientLogin(String variable) {
        super(ClientLoginDb.class, forVariable(variable), "public", "client_logins");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CLIENT_LOGIN;
    }

    @Override
    public NumberPath<Long> getId() {
        return id;
    }
}