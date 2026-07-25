package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod;

import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.PaymentMethodDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QPaymentMethod extends RelationalPathBase<PaymentMethodDb> implements QEntity {

    public static final QPaymentMethod PAYMENT_METHOD = new QPaymentMethod("payMd");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final StringPath module = createString("module");
    private final StringPath displayName = createString("display_name");
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final BooleanPath deleted = createBoolean("deleted");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            module,
            displayName,
            updatedAt,
            deleted
    );

    private QPaymentMethod(String variable) {
        super(PaymentMethodDb.class, forVariable(variable), "public", "payment_methods");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return PAYMENT_METHOD;
    }
}