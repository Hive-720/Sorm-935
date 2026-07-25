package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod;

import com.grunvald.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.QPaymentMethod.PAYMENT_METHOD;


@Getter
@Setter
public class PaymentMethodPersistenceFilter extends EntityFilter<PaymentMethodPersistenceFilter> {
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(PAYMENT_METHOD.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return PAYMENT_METHOD;
    }
}