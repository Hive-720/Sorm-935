package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.email;

import com.kjsoft.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QEmail extends RelationalPathBase<EmailDb> implements QEntity {

    public static final QEmail EMAIL = new QEmail("eml");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final NumberPath<Long> clientId = createNumber("client_id", Long.class);
    private final StringPath subject = createString("subject");
    private final StringPath messageBody = createString("message_body");
    private final StringPath to = createString("to");
    private final ArrayPath<String, String> cc = createArray("cc", String.class);
    private final ArrayPath<String, String> bcc = createArray("bcc", String.class);
    private final DateTimePath<OffsetDateTime> date = createDateTime("date", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> createdAt = createDateTime("created_at", OffsetDateTime.class);
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            clientId,
            subject,
            messageBody,
            to,
            cc,
            bcc,
            date,
            createdAt,
            updatedAt
    );


    private QEmail(String variable) {
        super(EmailDb.class, forVariable(variable), "public", "emails");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return EMAIL;
    }
}