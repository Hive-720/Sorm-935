package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.grunvald.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.QInvoice.INVOICE;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrder.ORDER;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrderItem.ORDER_ITEM;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class OrderPersistenceFilter extends EntityFilter<OrderPersistenceFilter> {
    private ClientPersistenceFilter client;
    private InvoicePersistenceFilter invoice;
    private OrderItemPersistenceFilter items;
    private DateTimeBaseFilter date;
    private IpAddressBaseFilter ipAddress;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, ORDER.getClientId(), CLIENT));
            }
        }

        if (invoice != null) {
            var invoicePredicate = invoice.toPredicate(joins);
            if (invoicePredicate != null) {
                conditions.add(invoicePredicate);
                joins.add(new Join(INNERJOIN, ORDER.getInvoiceId(), INVOICE));
            }
        }

        if (items != null) {
            var itemsPredicate = items.toPredicate(joins);
            if (itemsPredicate != null) {
                conditions.add(itemsPredicate);
                joins.add(new Join(INNERJOIN, ORDER.getId(), ORDER_ITEM.getOrderId(), ORDER_ITEM));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(ORDER.getDate()));
        }

        if (ipAddress != null) {
            conditions.add(ipAddress.toPredicate(ORDER.getIpAddress()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return ORDER;
    }
}