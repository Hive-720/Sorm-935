package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate;

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

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.QAffiliate.AFFILIATE;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class AffiliatePersistenceFilter extends EntityFilter<AffiliatePersistenceFilter> {
    private ClientPersistenceFilter client;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, AFFILIATE.getClientId(), CLIENT));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(AFFILIATE.getDate()));
        }

        if (createdAt != null) {
            conditions.add(createdAt.toPredicate(AFFILIATE.getCreatedAt()));
        }

        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(AFFILIATE.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return AFFILIATE;
    }
}