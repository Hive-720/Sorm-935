package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction;

import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.TransactionDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QTransaction extends RelationalPathBase<TransactionDb> implements QEntity {

    public static final QTransaction TRANSACTION = new QTransaction("trx");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> invoiceId = createNumber("invoice_id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final StringPath transId = createString("trans_id");
    private final NumberPath<Long> refundId = createNumber("refund_id", Long.class);
    private final StringPath currency = createString("currency");
    private final StringPath gateway = createString("gateway");
    private final StringPath description = createString("description");
    private final NumberPath<BigDecimal> amountIn = createNumber("amount_in", BigDecimal.class);
    private final NumberPath<BigDecimal> amountOut = createNumber("amount_out", BigDecimal.class);
    private final NumberPath<BigDecimal> fees = createNumber("fees", BigDecimal.class);
    private final NumberPath<BigDecimal> rate = createNumber("rate", BigDecimal.class);
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            invoiceId,
            clientId,
            transId,
            refundId,
            currency,
            gateway,
            description,
            amountIn,
            amountOut,
            fees,
            rate,
            date
    );

    private QTransaction(String variable) {
        super(TransactionDb.class, forVariable(variable), "public", "transactions");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return TRANSACTION;
    }
}