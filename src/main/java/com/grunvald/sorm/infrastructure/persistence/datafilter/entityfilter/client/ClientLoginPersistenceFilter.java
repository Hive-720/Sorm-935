package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientLogin.CLIENT_LOGIN;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class ClientLoginPersistenceFilter extends EntityFilter<ClientLoginPersistenceFilter> {
    private ClientPersistenceFilter client;
    private IpAddressBaseFilter ipAddress;
    private DateTimeBaseFilter date;


    @Override
    public QEntity getQEntity() {
        return CLIENT_LOGIN;
    }

    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, CLIENT_LOGIN.getClientId(), CLIENT));
            }
        }

        if (ipAddress != null) {
            conditions.add(ipAddress.toPredicate(CLIENT_LOGIN.getIpAddress()));
        }

        if (date != null) {
            conditions.add(date.toPredicate(CLIENT_LOGIN.getDate()));
        }
    }
}