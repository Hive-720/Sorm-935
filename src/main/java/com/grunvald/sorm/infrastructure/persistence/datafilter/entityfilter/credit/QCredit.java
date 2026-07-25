package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit;

import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QCredit extends RelationalPathBase<CreditDb> implements QEntity {

    public static final QCredit CREDIT = new QCredit("crd");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> relId = createNumber("rel_id", Long.class);
    private final StringPath description = createString("description");
    private final NumberPath<BigDecimal> amount = createNumber("amount", BigDecimal.class);
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            relId,
            description,
            amount,
            date
    );

    private QCredit(String variable) {
        super(CreditDb.class, forVariable(variable), "public", "credits");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CREDIT;
    }
}