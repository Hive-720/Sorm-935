package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public final class QClient extends RelationalPathBase<ClientDb> implements QEntity {

    public static final QClient CLIENT = new QClient("cl");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientGroupId = createNumber("client_group_id", Long.class);
    private final NumberPath<Long> ownerUserId = createNumber("owner_user_id", Long.class);
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
    private final NumberPath<Integer> phonecc = createNumber("phonecc", Integer.class);
    private final StringPath phoneNumber = createString("phone_number");
    private final StringPath taxId = createString("tax_id");
    private final StringPath telegram = createString("telegram");
    private final StringPath jabber = createString("jabber");
    private final StringPath currency = createString("currency");
    private final StringPath defaultGateway = createString("default_gateway");
    private final StringPath status = createString("status");
    private final StringPath credit = createString("credit");
    private final BooleanPath taxExempt = createBoolean("tax_exempt");
    private final StringPath language = createString("language");
    private final DateTimePath<OffsetDateTime> dateCreated = createDateTime("date_created", OffsetDateTime.class);
    private final BooleanPath deleted = createBoolean("deleted");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientGroupId,
            ownerUserId,
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
            phonecc,
            phoneNumber,
            taxId,
            telegram,
            jabber,
            currency,
            defaultGateway,
            status,
            credit,
            taxExempt,
            language,
            dateCreated,
            deleted
    );

    private QClient(String variable) {
        super(ClientDb.class, forVariable(variable), "public", "clients");
    }

    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return CLIENT;
    }
}