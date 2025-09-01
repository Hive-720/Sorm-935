package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.QInvoice.INVOICE;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class InvoicePersistenceFilter extends EntityFilter<InvoicePersistenceFilter> {
    private ClientPersistenceFilter client;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter dueDate;
    private DateTimeBaseFilter datePaid;
    private DateTimeBaseFilter dateRefunded;
    private DateTimeBaseFilter dateCancelled;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var predicate = client.toPredicate(joins);
            if (predicate != null) {
                conditions.add(predicate);
                joins.add(new Join(INNERJOIN, INVOICE.getClientId(), CLIENT));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(INVOICE.getDate()));
        }

        if (dueDate != null) {
            conditions.add(dueDate.toPredicate(INVOICE.getDueDate()));
        }

        if (datePaid != null) {
            conditions.add(datePaid.toPredicate(INVOICE.getDatePaid()));
        }

        if (dateRefunded != null) {
            conditions.add(dateRefunded.toPredicate(INVOICE.getDateRefunded()));
        }

        if (dateCancelled != null) {
            conditions.add(dateCancelled.toPredicate(INVOICE.getDateCancelled()));
        }

        if (createdAt != null) {
            conditions.add(createdAt.toPredicate(INVOICE.getCreatedAt()));
        }

        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(INVOICE.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return INVOICE;
    }
}