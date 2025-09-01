package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrderItem.ORDER_ITEM;


@Getter
@Setter
public class OrderItemPersistenceFilter extends EntityFilter<OrderItemPersistenceFilter> {
    private DomainNameBaseFilter domain;

    @Override
    public QEntity getQEntity() {
        return ORDER_ITEM;
    }

    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (domain != null) {
            conditions.add(domain.toPredicate(ORDER_ITEM.getDomain()));
        }
    }
}