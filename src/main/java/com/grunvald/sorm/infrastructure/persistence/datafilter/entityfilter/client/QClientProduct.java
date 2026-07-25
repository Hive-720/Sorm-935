package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientProductDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QClientProduct extends RelationalPathBase<ClientProductDb> implements QEntity {

    public static final QClientProduct CLIENT_PRODUCT = new QClientProduct("clPrd");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<Long> productId = createNumber("product_id", Long.class);
    private final NumberPath<Long> orderId = createNumber("order_id", Long.class);
    private final DateTimePath<OffsetDateTime> regDate = createDateTime("reg_date", OffsetDateTime.class);
    private final StringPath name = createString("name");
    private final StringPath domain = createString("domain");
    private final StringPath dedicatedIpAddress = createString("dedicated_ip_address");
    private final NumberPath<BigDecimal> firstPaymentAmount = createNumber("first_payment_amount", BigDecimal.class);
    private final NumberPath<BigDecimal> recurringAmount = createNumber("recurring_amount", BigDecimal.class);
    private final StringPath billingCycle = createString("billing_cycle");
    private final DateTimePath<OffsetDateTime> nextDueDate = createDateTime("next_due_date", OffsetDateTime.class);
    private final ArrayPath<String, String> assignedIps = createArray("assigned_ips", String.class);
    private final StringPath status = createString("status");
    private final DateTimePath<OffsetDateTime> lastUpdate = createDateTime("last_update", OffsetDateTime.class);
    private final StringPath configs = createString("configs");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            productId,
            orderId,
            regDate,
            name,
            domain,
            dedicatedIpAddress,
            firstPaymentAmount,
            recurringAmount,
            billingCycle,
            nextDueDate,
            assignedIps,
            status,
            lastUpdate,
            configs
    );


    private QClientProduct(String variable) {
        super(ClientProductDb.class, forVariable(variable), "public", "client_products");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CLIENT_PRODUCT;
    }
}