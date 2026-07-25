package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QAffiliate extends RelationalPathBase<Affiliate> implements QEntity {

    public final static QAffiliate AFFILIATE = new QAffiliate("qAff");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> visitors = createNumber("visitors", Long.class);
    private final StringPath payType = createString("pay_type");
    private final NumberPath<BigDecimal> payAmount = createNumber("pay_amount", BigDecimal.class);
    private final NumberPath<BigDecimal> balance = createNumber("balance", BigDecimal.class);
    private final NumberPath<BigDecimal> withdrawn = createNumber("withdrawn", BigDecimal.class);
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> createdAt = createDateTime("created_at", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);

    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            visitors,
            payType,
            payAmount,
            balance,
            withdrawn,
            date,
            createdAt,
            updatedAt
    );

    public QAffiliate(String variable) {
        super(Affiliate.class, forVariable(variable), "public", "affiliates");
    }


    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return AFFILIATE;
    }
}
