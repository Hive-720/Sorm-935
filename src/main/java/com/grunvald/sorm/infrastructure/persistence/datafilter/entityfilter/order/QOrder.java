package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QOrder extends RelationalPathBase<OrderDb> implements QEntity {

    public static final QOrder ORDER = new QOrder("ord");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> contactId = createNumber("client_id", Long.class);
    private final NumberPath<Long> invoiceId = createNumber("client_id", Long.class);
    private final NumberPath<BigDecimal> amount = createNumber("client_id", BigDecimal.class);
    private final StringPath currency = createString("currency");
    private final StringPath paymentMethod = createString("payment_method");
    private final StringPath status = createString("status");
    private final StringPath ipAddress = createString("ip_address");
    private final StringPath paymentMethodName = createString("payment_method_name");
    private final StringPath paymentStatus = createString("payment_status");
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            contactId,
            invoiceId,
            amount,
            currency,
            paymentMethod,
            status,
            ipAddress,
            paymentMethodName,
            paymentStatus,
            date
    );

    private QOrder(String variable) {
        super(OrderDb.class, forVariable(variable), "public", "orders");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return ORDER;
    }
}