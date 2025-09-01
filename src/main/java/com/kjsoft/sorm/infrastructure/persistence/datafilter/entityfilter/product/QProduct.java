package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product;

import com.kjsoft.sorm.infrastructure.persistence.data.product.entity.ProductDb;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.RelationalPathBase;
import lombok.Getter;

import java.time.OffsetDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


@Getter
public class QProduct extends RelationalPathBase<ProductDb> implements QEntity {

    public static final QProduct PRODUCT = new QProduct("prd");

    private final NumberPath<Long> id = createNumber("id", Long.class);
    private final StringPath typeField = createString("type");
    private final StringPath name = createString("name");
    private final StringPath description = createString("description");
    private final StringPath payType = createString("pay_type");
    private final StringPath prices = createString("prices");
    private final DateTimePath<OffsetDateTime> updatedAt = createDateTime("updated_at", OffsetDateTime.class);
    private final BooleanPath deleted = createBoolean("deleted");
    private final SimpleExpression<Tuple> FIELDS = Expressions.list(
            id,
            typeField,
            name,
            description,
            payType,
            prices,
            updatedAt,
            deleted
    );


    private QProduct(String variable) {
        super(ProductDb.class, forVariable(variable), "public", "products");
    }

    @Override
    public SimpleExpression<Tuple> getFieldsForSelectSql() {
        return FIELDS;
    }

    @Override
    public RelationalPathBase<?> getRelationalPathBase() {
        return PRODUCT;
    }
}