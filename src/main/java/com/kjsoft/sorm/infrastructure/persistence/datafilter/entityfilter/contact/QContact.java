package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.contact;

import com.kjsoft.sorm.infrastructure.persistence.data.contact.entity.ContactDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QContact extends RelationalPathBase<ContactDb> implements QEntity {

    public static final QContact CONTACT = new QContact("cnt");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final StringPath fullName = createString("full_name");
    private final StringPath firstName = createString("first_name");
    private final StringPath lastName = createString("last_name");
    private final StringPath companyName = createString("company_name");
    private final StringPath email = createString("email");
    private final StringPath address = createString("address");
    private final StringPath city = createString("city");
    private final StringPath state = createString("state");
    private final StringPath postcode = createString("postcode");
    private final StringPath country = createString("country");
    private final StringPath msisdn = createString("msisdn");
    private final StringPath taxId = createString("tax_id");
    private final DateTimePath<OffsetDateTime> createdAt = createDateTime("created_at", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final BooleanPath deleted = createBoolean("deleted");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            fullName,
            firstName,
            lastName,
            companyName,
            email,
            address,
            city,
            state,
            postcode,
            country,
            msisdn,
            taxId,
            createdAt,
            updatedAt,
            deleted
    );

    private QContact(String variable) {
        super(ContactDb.class, forVariable(variable), "public", "contacts");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CONTACT;
    }
}