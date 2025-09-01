package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.EmailBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.QUser.USER;


@Getter
@Setter
public class UserPersistenceFilter extends EntityFilter<UserPersistenceFilter> {
    private PersonBaseFilter name;
    private EmailBaseFilter email;


    @Override
    public void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (name != null) {
            conditions.add(name.toPredicate(USER.getFullName(), USER.getFirstName(), USER.getLastName()));
        }

        if (email != null) {
            conditions.add(email.toPredicate(USER.getEmail()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return USER;
    }
}