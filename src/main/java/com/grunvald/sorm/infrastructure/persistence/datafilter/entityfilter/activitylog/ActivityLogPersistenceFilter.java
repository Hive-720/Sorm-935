package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog;

import com.grunvald.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QEntity;
import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.LoginFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.QActivityLog.ACTIVITY_LOG;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class ActivityLogPersistenceFilter extends EntityFilter<ActivityLogPersistenceFilter> {
    private ClientPersistenceFilter client;
    private UserPersistenceFilter user;
    private DateTimeBaseFilter date;
    private LoginFilter username;
    private IpAddressBaseFilter ipAddress;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var clientPredicate = client.toPredicate(joins);
            if (clientPredicate != null) {
                conditions.add(clientPredicate);
                joins.add(new Join(INNERJOIN, ACTIVITY_LOG.getClientId(), CLIENT));
            }
        }

        if (user != null) {
            var userPredicate = user.toPredicate(joins);
            if (userPredicate != null) {
                conditions.add(userPredicate);
                joins.add(new Join(INNERJOIN, ACTIVITY_LOG.getClientId(), USER));
            }
        }

        if (date != null) {
            conditions.add(date.toPredicate(ACTIVITY_LOG.getDate()));
        }

        if (username != null) {
            conditions.add(username.toPredicate(ACTIVITY_LOG.getUsername()));
        }

        if (ipAddress != null) {
            conditions.add(ipAddress.toPredicate(ACTIVITY_LOG.getIpAddress()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return ACTIVITY_LOG;
    }
}