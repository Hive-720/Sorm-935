package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.EntityFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QEntity;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

import static com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.product.QProduct.PRODUCT;


@Getter
@Setter
public class ProductPersistenceFilter extends EntityFilter<ProductPersistenceFilter> {
    private DateTimeBaseFilter updatedAt;


    @Override
    protected void processPredicate(Deque<Join> joins, List<BooleanExpression> conditions) {
        if (updatedAt != null) {
            conditions.add(updatedAt.toPredicate(PRODUCT.getUpdatedAt()));
        }
    }

    @Override
    public QEntity getQEntity() {
        return PRODUCT;
    }
}