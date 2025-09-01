package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.contact;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.*;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.contact.QContact.CONTACT;
import static com.querydsl.core.JoinType.INNERJOIN;


@Getter
@Setter
public class ContactFilter extends EntityFilter<ContactFilter> {
    private ClientPersistenceFilter client;
    private PersonBaseFilter name;
    private OrganizationInfoBaseFilter organizationInfo;
    private EmailBaseFilter email;
    private AddressBaseFilter address;
    private InnBaseFilter inn;
    private MsisdnBaseFilter msisdn;
    private DateTimeBaseFilter updatedAt;
    private DateTimeBaseFilter createdAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (client != null) {
            var predicate = client.toPredicate(joins);
            if (predicate != null) {
                conditions.add(predicate);
                joins.add(new Join(INNERJOIN, CONTACT.getClientId(), CLIENT));
            }
        }

        if (name != null) {
            conditions.add(name.toPredicate(CONTACT.getFullName(), CONTACT.getFirstName(), CONTACT.getLastName()));
        }

        if (organizationInfo != null) {
            conditions.add(organizationInfo.toPredicate(CONTACT.getCompanyName()));
        }

        if (email != null) {
            conditions.add(email.toPredicate(CONTACT.getEmail()));
        }

        if (address != null) {
            conditions.add(address.toPredicate(CONTACT.getAddress(), CONTACT.getPostcode(), CONTACT.getCountry(),
                    CONTACT.getState(), CONTACT.getCity())
            );
        }

        if (inn != null) {
            conditions.add(inn.toPredicate(CONTACT.getTaxId()));
        }

        if (msisdn != null) {
            conditions.add(msisdn.toPredicate(CONTACT.getMsisdn()));
        }

        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(CONTACT.getUpdatedAt()));
        }

        if (createdAt != null) {
            conditions.add(createdAt.toPredicate(CONTACT.getCreatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return CONTACT;
    }
}