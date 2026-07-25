package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientGroup.CLIENT_GROUP;


@Getter
@Setter
public class ClientGroupPersistenceFilter extends EntityFilter<ClientGroupPersistenceFilter> {
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(CLIENT_GROUP.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return CLIENT_GROUP;
    }
}