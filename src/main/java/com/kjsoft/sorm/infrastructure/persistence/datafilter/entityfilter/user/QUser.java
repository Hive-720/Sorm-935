package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user;

import com.kjsoft.sorm.infrastructure.persistence.data.user.entity.UserDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QUser extends RelationalPathBase<UserDb> implements QEntity {

    public static final QUser USER = new QUser("u");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final StringPath fullName = createString("full_name");
    private final StringPath firstName = createString("first_name");
    private final StringPath lastName = createString("last_name");
    private final StringPath email = createString("email");
    private final DateTimePath<OffsetDateTime> dateCreated = createDateTime("date_created", OffsetDateTime.class);

    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            fullName,
            firstName,
            lastName,
            email,
            dateCreated
    );

    public QUser(String variable) {
        super(UserDb.class, forVariable(variable), "public", "users");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<UserDb> getRelationalPathBase() {
        return USER;
    }
}