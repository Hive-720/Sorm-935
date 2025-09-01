package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientGroupDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public final class QClientGroup extends RelationalPathBase<ClientGroupDb> implements QEntity {

    public static QClientGroup CLIENT_GROUP = new QClientGroup("clGr");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final StringPath name = createString("name");
    private final NumberPath<BigDecimal> discountPercent = createNumber("discount_percent", BigDecimal.class);
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final BooleanPath deleted = createBoolean("deleted");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            name,
            discountPercent,
            updatedAt,
            deleted
    );


    private QClientGroup(String variable) {
        super(ClientGroupDb.class, forVariable(variable), "public", "client_groups");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CLIENT_GROUP;
    }
}