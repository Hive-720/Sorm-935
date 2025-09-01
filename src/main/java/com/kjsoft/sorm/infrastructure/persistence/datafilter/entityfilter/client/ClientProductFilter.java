package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.OrderPersistenceFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product.ProductPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientProduct.CLIENT_PRODUCT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrder.ORDER;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product.QProduct.PRODUCT;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class ClientProductFilter extends EntityFilter<ClientProductFilter> {
    private ClientPersistenceFilter client;
    private OrderPersistenceFilter order;
    private ProductPersistenceFilter product;
    private DateTimeBaseFilter regDate;
    private DomainNameBaseFilter domain;
    private IpAddressBaseFilter dedicatedIpAddress;
    private IpAddressBaseFilter assignedIpAddresses;
    private DateTimeBaseFilter nextDueDate;
    private DateTimeBaseFilter lastUpdate;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, CLIENT_PRODUCT.getClientId(), CLIENT));
            }
        }

        if (order != null) {
            var orderPredicate = order.toPredicate(joins);
            if (orderPredicate != null) {
                conditions.add(orderPredicate);
                joins.add(new Join(INNERJOIN, CLIENT_PRODUCT.getOrderId(), ORDER));
            }
        }

        if (product != null) {
            var productPredicate = product.toPredicate(joins);
            if (productPredicate != null) {
                conditions.add(productPredicate);
                joins.add(new Join(INNERJOIN, CLIENT_PRODUCT.getProductId(), PRODUCT));
            }
        }

        if (regDate != null) {
            conditions.add(regDate.toPredicate(CLIENT_PRODUCT.getRegDate()));
        }

        if (domain != null) {
            conditions.add(domain.toPredicate(CLIENT_PRODUCT.getDomain()));
        }

        if (dedicatedIpAddress != null) {
            conditions.add(dedicatedIpAddress.toPredicate(CLIENT_PRODUCT.getDedicatedIpAddress()));
        }

        if (assignedIpAddresses != null) {
            conditions.add(assignedIpAddresses.toPredicate(CLIENT_PRODUCT.getAssignedIps()));
        }

        if (nextDueDate != null) {
            conditions.add(nextDueDate.toPredicate(CLIENT_PRODUCT.getNextDueDate()));
        }

        if (lastUpdate != null) {
            conditions.add(lastUpdate.toPredicate(CLIENT_PRODUCT.getLastUpdate()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return CLIENT_PRODUCT;
    }
}