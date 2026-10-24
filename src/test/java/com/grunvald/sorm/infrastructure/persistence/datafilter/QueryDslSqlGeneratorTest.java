package com.grunvald.sorm.infrastructure.persistence.datafilter;

import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.OrderPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.PaymentMethodPersistenceFilter;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryDslSqlGeneratorTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    private final QueryDslSqlGenerator generator = new QueryDslSqlGenerator(
            new Configuration(PostgreSQLTemplates.builder().build())
    );

    @Test
    void generateSelect_withoutCursorAndOffset_producesSelectWhereOrderByLimit() {
        var result = generator.generateSelect(filterByUpdatedAt(), null, null, 10);

        var sql = result.getLeft();
        assertTrue(sql.contains("select payMd.id"));
        assertTrue(sql.contains("from payment_methods payMd"));
        assertTrue(sql.contains("payMd.updated_at = $1"));
        assertTrue(sql.contains("order by payMd.id asc"));
        assertTrue(sql.contains("limit $2"));
        assertFalse(sql.contains("offset"));

        assertEquals(List.of(DATE, 10L), result.getRight());
    }

    @Test
    void generateSelect_withOffset_addsOffset() {
        var result = generator.generateSelect(filterByUpdatedAt(), null, 5, 10);

        var sql = result.getLeft();
        assertTrue(sql.contains("limit $2"));
        assertTrue(sql.contains("offset $3"));

        assertEquals(List.of(DATE, 10L, 5L), result.getRight());
    }

    @Test
    void generateSelect_withCursor_addsIdGreaterThanCursor() {
        var result = generator.generateSelect(filterByUpdatedAt(), "42", null, 10);

        var sql = result.getLeft();
        assertTrue(sql.contains("payMd.updated_at = $1"));
        assertTrue(sql.contains("payMd.id > $2"));
        assertTrue(sql.contains("limit $3"));
        assertFalse(sql.contains("offset"));

        assertEquals(List.of(DATE, 42L, 10L), result.getRight());
    }

    @Test
    void generateSelect_withJoin_rendersRootTableFirstWithInnerJoin() {
        var filter = new OrderPersistenceFilter();
        var client = new ClientPersistenceFilter();
        client.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, null, null), null, null, null, null, null
        ));
        filter.setClient(client);

        var result = generator.generateSelect(filter, null, null, 10);

        var sql = result.getLeft();
        assertTrue(sql.contains("from orders ord"));
        assertTrue(sql.contains("inner join clients cl"));
        assertTrue(sql.contains("on ord.client_id = cl.id"));
        assertTrue(sql.contains("to_tsquery('simple', $1)"));

        assertEquals(List.of("john", 10L), result.getRight());
    }

    private static PaymentMethodPersistenceFilter filterByUpdatedAt() {
        var filter = new PaymentMethodPersistenceFilter();
        filter.setUpdatedAt(new DateTimeBaseFilter(
                new DateTimeCondition(DATE, null, null, null, null, null, null), null, null, null
        ));
        return filter;
    }
}
