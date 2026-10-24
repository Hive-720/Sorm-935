package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction;

import com.grunvald.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator.Join;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.DateTimeBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.DateTimeCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermFullTextCondition;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.InvoicePersistenceFilter;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.QClient.CLIENT;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.QInvoice.INVOICE;
import static com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.QTransaction.TRANSACTION;
import static com.querydsl.core.JoinType.INNERJOIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionPersistenceFilterTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Test
    void allNulls_returnsNull() {
        var filter = new TransactionPersistenceFilter();
        var joins = new ArrayDeque<Join>();

        assertNull(filter.toPredicate(joins));
        assertTrue(joins.isEmpty());
        assertEquals(TRANSACTION, filter.getQEntity());
    }

    @Test
    void allFields_addConditionsAndJoins() {
        var filter = new TransactionPersistenceFilter();
        var invoice = new InvoicePersistenceFilter();
        invoice.setUpdatedAt(dateFilter(DATE));
        filter.setInvoice(invoice);
        filter.setClient(clientByName("john"));
        filter.setDate(dateFilter(DATE.plusDays(1)));

        var joins = new ArrayDeque<Join>();
        var sql = filter.toPredicate(joins).toString();

        assertTrue(sql.contains("= 2026-01-02T03:04:05Z"));
        assertTrue(sql.contains("to_tsquery('simple', john)"));
        assertTrue(sql.contains("= 2026-01-03T03:04:05Z"));

        assertEquals(2, joins.size());
        var invoiceJoin = joins.pollFirst();
        assertEquals(INNERJOIN, invoiceJoin.getType());
        assertEquals(TRANSACTION.getInvoiceId(), invoiceJoin.getSourceId());
        assertEquals(INVOICE, invoiceJoin.getTargetEntity());

        var clientJoin = joins.pollFirst();
        assertEquals(INNERJOIN, clientJoin.getType());
        assertEquals(TRANSACTION.getClientId(), clientJoin.getSourceId());
        assertEquals(CLIENT, clientJoin.getTargetEntity());
    }

    private static ClientPersistenceFilter clientByName(String term) {
        var client = new ClientPersistenceFilter();
        client.setName(new PersonBaseFilter(
                new StringTermFullTextCondition(List.of(term), null, null, null), null, null, null, null, null
        ));
        return client;
    }

    private static DateTimeBaseFilter dateFilter(OffsetDateTime date) {
        return new DateTimeBaseFilter(
                new DateTimeCondition(date, null, null, null, null, null, null), null, null, null
        );
    }
}
