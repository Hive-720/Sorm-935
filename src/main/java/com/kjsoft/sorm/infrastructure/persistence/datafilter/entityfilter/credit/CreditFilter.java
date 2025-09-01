package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.credit;

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
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.credit.QCredit.CREDIT;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class CreditFilter extends EntityFilter<CreditFilter> {
    private ClientPersistenceFilter client;
    private DateTimeBaseFilter date;

    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var predicate = client.toPredicate(joins);
            if (predicate != null) {
                conditions.add(predicate);
                joins.add(new Join(INNERJOIN, CREDIT.getClientId(), CLIENT));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(CREDIT.getDate()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return CREDIT;
    }
}