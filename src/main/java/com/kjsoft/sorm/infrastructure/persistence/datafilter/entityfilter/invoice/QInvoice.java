package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice;

import com.kjsoft.sorm.infrastructure.persistence.data.invoice.entity.InvoiceDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QInvoice extends RelationalPathBase<InvoiceDb> implements QEntity {

    public static final QInvoice INVOICE = new QInvoice("invs");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final NumberPath<BigDecimal> subTotal = createNumber("sub_total", BigDecimal.class);
    private final NumberPath<BigDecimal> total = createNumber("total", BigDecimal.class);
    private final NumberPath<BigDecimal> credit = createNumber("credit", BigDecimal.class);
    private final NumberPath<BigDecimal> balance = createNumber("balance", BigDecimal.class);
    private final NumberPath<BigDecimal> tax = createNumber("tax", BigDecimal.class);
    private final NumberPath<BigDecimal> tax2 = createNumber("tax2", BigDecimal.class);
    private final NumberPath<BigDecimal> taxRate = createNumber("tax_rate", BigDecimal.class);
    private final NumberPath<BigDecimal> taxRate2 = createNumber("tax_rate2", BigDecimal.class);
    private final StringPath status = createString("status");
    private final StringPath currency = createString("currency");
    private final StringPath paymentMethod = createString("payment_method");
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> dueDate = createDateTime("due_date", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> datePaid = createDateTime("date_paid", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> dateRefunded = createDateTime("date_refunded", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> dateCancelled = createDateTime("date_cancelled", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> createdAt = createDateTime("created_at", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final StringPath items = createString("items");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            subTotal,
            total,
            credit,
            balance,
            tax,
            tax2,
            taxRate,
            taxRate2,
            status,
            currency,
            paymentMethod,
            date,
            dueDate,
            datePaid,
            dateRefunded,
            dateCancelled,
            createdAt,
            updatedAt,
            items
    );

    private QInvoice(String variable) {
        super(InvoiceDb.class, forVariable(variable), "public", "invoices");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return INVOICE;
    }
}