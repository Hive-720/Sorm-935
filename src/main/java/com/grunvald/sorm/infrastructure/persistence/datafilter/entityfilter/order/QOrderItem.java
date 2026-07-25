package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.grunvald.sorm.infrastructure.persistence.data.order.entity.OrderItemDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QOrderItem extends RelationalPathBase<OrderItemDb> implements QEntity {

    public static final QOrderItem ORDER_ITEM = new QOrderItem("ordIt");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> orderId = createNumber("order_id", Long.class);
    private final NumberPath<Long> relId = createNumber("rel_id", Long.class);
    private final StringPath typeName = createString("type");
    private final StringPath productType = createString("product_type");
    private final StringPath productName = createString("product_name");
    private final StringPath domain = createString("domain");
    private final StringPath billingCycle = createString("billing_cycle");
    private final NumberPath<BigDecimal> amount = createNumber("amount", BigDecimal.class);
    private final StringPath status = createString("status");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            orderId,
            relId,
            typeName,
            productType,
            productName,
            domain,
            billingCycle,
            amount,
            status
    );

    private QOrderItem(String variable) {
        super(OrderItemDb.class, forVariable(variable), "public", "order_items");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return ORDER_ITEM;
    }

    @Override
    public NumberPath<Long> getId() {
        return id;
    }
}