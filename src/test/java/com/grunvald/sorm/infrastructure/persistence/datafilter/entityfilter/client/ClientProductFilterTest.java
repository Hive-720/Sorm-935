package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DomainNameBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.IpAddressBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringRegExpExactCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.OrderPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.ProductPersistenceFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.IpType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClientProduct.CLIENT_PRODUCT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.order.QOrder.ORDER;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.product.QProduct.PRODUCT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientProductFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new ClientProductFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(CLIENT_PRODUCT, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndJoins() {
        var filter = new ClientProductFilter();
        var client = new ClientPersistenceFilter();
        client.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of("john"), null, null, null), null, null, null, null, null
        ));
        filter.setClient(client);

        var order = new OrderPersistenceFilter();
        order.setDate(dateFilter(DATE));
        filter.setOrder(order);

        var product = new ProductPersistenceFilter();
        product.setUpdatedAt(dateFilter(DATE.plusDays(1)));
        filter.setProduct(product);

        filter.setRegDate(dateFilter(DATE.plusDays(2)));
        filter.setDomain(new DomainNameBaseFilter(
                new StringRegExpExactCondition(null, "example.com", null), null, null, null
        ));
        filter.setDedicatedIpAddress(new IpAddressBaseFilter(null, IpType.IPV4, null, null, null));
        filter.setAssignedIpAddresses(new IpAddressBaseFilter(null, IpType.IPV6, null, null, null));
        filter.setNextDueDate(dateFilter(DATE.plusDays(3)));
        filter.setLastUpdate(dateFilter(DATE.plusDays(4)));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-03T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-04T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-05T03:04:05Z"));
        assertTrue(sql.contains("= 2026-01-06T03:04:05Z"));
        assertTrue(sql.contains("= example.com"));
        assertTrue(sql.contains("like %:%"));
        assertTrue(sql.contains("%:% LIKE ANY"));

        assertEquals(3, joins.size());
        var clientJoin = joins.pollFirst();
        assertEquals(CLIENT_PRODUCT.getClientId(), clientJoin.getSourceId());
        assertEquals(CLIENT, clientJoin.getTargetEntity());

        var orderJoin = joins.pollFirst();
        assertEquals(CLIENT_PRODUCT.getOrderId(), orderJoin.getSourceId());
        assertEquals(ORDER, orderJoin.getTargetEntity());

        var productJoin = joins.pollFirst();
        assertEquals(CLIENT_PRODUCT.getProductId(), productJoin.getSourceId());
        assertEquals(PRODUCT, productJoin.getTargetEntity());
    }

    private static DateTimeBaseFilter dateFilter(OffsetDateTime date) {
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
