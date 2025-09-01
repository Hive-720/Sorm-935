package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.*;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientGroup.CLIENT_GROUP;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class ClientPersistenceFilter extends EntityFilter<ClientPersistenceFilter> {
    private UserPersistenceFilter userOwner;
    private ClientGroupPersistenceFilter clientGroup;
    private PersonBaseFilter name;
    private OrganizationInfoBaseFilter organizationInfo;
    private InnBaseFilter inn;
    private EmailBaseFilter email;
    private MsisdnBaseFilter msisdn;
    private MccBaseFilter mcc;
    private AddressBaseFilter address;


    @Override
    public void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (userOwner != null) {
            var userPredicate = userOwner.toPredicate(joins);
            if (userPredicate != null) {
                conditions.add(userPredicate);
                joins.add(new Join(INNERJOIN, CLIENT.getOwnerUserId(), USER));
            }
        }

        if (clientGroup != null) {
            var clientGroupPredicate = clientGroup.toPredicate(joins);
            if (clientGroupPredicate != null) {
                conditions.add(clientGroupPredicate);
                joins.add(new Join(INNERJOIN, CLIENT.getClientGroupId(), CLIENT_GROUP));
            }
        }

        if (name != null) {
            conditions.add(name.toPredicate(CLIENT.getFullName(), CLIENT.getFirstName(), CLIENT.getLastName()));
        }

        if (organizationInfo != null) {
            conditions.add(organizationInfo.toPredicate(CLIENT.getCompanyName()));
        }

        if (inn != null) {
            conditions.add(inn.toPredicate(CLIENT.getTaxId()));
        }

        if (email != null) {
            conditions.add(email.toPredicate(CLIENT.getEmail()));
        }

        if (msisdn != null) {
            conditions.add(msisdn.toPredicate(CLIENT.getMsisdn()));
        }

        if (mcc != null) {
            conditions.add(mcc.toPredicate(CLIENT.getPhonecc()));
        }

        if (address != null) {
            conditions.add(address.toPredicate(CLIENT.getAddress(), CLIENT.getPostcode(), CLIENT.getCountry(),
                    CLIENT.getState(), CLIENT.getCity())
            );
        }
    }

    @Override
    public QEntity getQEntity() {
        return CLIENT;
    }
}