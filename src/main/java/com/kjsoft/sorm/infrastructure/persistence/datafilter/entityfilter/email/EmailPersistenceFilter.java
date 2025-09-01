package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.email;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.email.QEmail.EMAIL;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class EmailPersistenceFilter extends EntityFilter<EmailPersistenceFilter> {
    private ClientPersistenceFilter client;
    private EmailBaseFilter to;
    private EmailBaseFilter cc;
    private EmailBaseFilter bcc;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var predicate = client.toPredicate(joins);
            if (predicate != null) {
                conditions.add(predicate);
                joins.add(new Join(INNERJOIN, EMAIL.getClientId(), CLIENT));
            }
        }

        if (to != null) {
            conditions.add(to.toPredicate(EMAIL.getTo()));
        }

        if (cc != null) {
            conditions.add(cc.toPredicate(EMAIL.getCc())); //TODO посмотреть что с массивом
        }

        if (bcc != null) {
            conditions.add(bcc.toPredicate(EMAIL.getBcc()));
        }

        if (date != null) {
            conditions.add(date.toPredicate(EMAIL.getDate()));
        }

        if (createdAt != null) {
            conditions.add(createdAt.toPredicate(EMAIL.getCreatedAt()));
        }

        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(EMAIL.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return EMAIL;
    }
}