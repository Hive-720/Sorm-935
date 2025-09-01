package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.transaction;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.QInvoice.INVOICE;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.QTransaction.TRANSACTION;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class TransactionPersistenceFilter extends EntityFilter<TransactionPersistenceFilter> {
    private InvoicePersistenceFilter invoice;
    private ClientPersistenceFilter client;
    private DateTimeBaseFilter date;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (invoice != null) {
            var invoicePredicate = invoice.toPredicate(joins);
            if (invoicePredicate != null) {
                conditions.add(invoicePredicate);
                joins.add(new Join(INNERJOIN, TRANSACTION.getInvoiceId(), INVOICE));
            }
        }

        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, TRANSACTION.getClientId(), CLIENT));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(TRANSACTION.getDate()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return TRANSACTION;
    }
}