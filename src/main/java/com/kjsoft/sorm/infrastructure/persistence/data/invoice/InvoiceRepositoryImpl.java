package com.kjsoft.sorm.infrastructure.persistence.data.invoice;

import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.InvoiceRepository;
import com.kjsoft.sorm.service.whmcsdata.filter.InvoiceFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.kjsoft.sorm.service.whmcsdata.dataservice.invoice.dto.InvoiceSearchResult;
import com.kjsoft.sorm.infrastructure.persistence.data.invoice.entity.InvoiceDb;
import com.kjsoft.sorm.infrastructure.persistence.data.invoice.entity.mapper.InvoicePersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.mapper.InvoiceFilterPersistenceMapper;
import io.r2dbc.postgresql.codec.Json;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kjsoft.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class InvoiceRepositoryImpl implements InvoiceRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final InvoicePersistenceMapper invoiceMapper;
    private final InvoiceFilterPersistenceMapper invoiceFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Invoice> invoices) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO invoices
                    (id, client_id, sub_total, total, credit, balance, tax, tax2,
                     tax_rate, tax_rate2, status, currency, payment_method, items, date, due_date,
                     date_paid, date_refunded, date_cancelled, created_at, updated_at)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19, $20, $21)
                    """);

            return Flux.fromIterable(invoices)
                    .map(invoiceMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getSubTotal(), BigDecimal.class);
                        bindOrNull(stmt, 3, entity.getTotal(), BigDecimal.class);
                        bindOrNull(stmt, 4, entity.getCredit(), BigDecimal.class);
                        bindOrNull(stmt, 5, entity.getBalance(), BigDecimal.class);
                        bindOrNull(stmt, 6, entity.getTax(), BigDecimal.class);
                        bindOrNull(stmt, 7, entity.getTax2(), BigDecimal.class);
                        bindOrNull(stmt, 8, entity.getTaxRate(), BigDecimal.class);
                        bindOrNull(stmt, 9, entity.getTaxRate2(), BigDecimal.class);
                        bindOrNull(stmt, 10, entity.getStatus(), String.class);
                        bindOrNull(stmt, 11, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 12, entity.getPaymentMethod(), String.class);
                        bindOrNull(stmt, 13, entity.getItems(), Json.class);
                        bindOrNull(stmt, 14, entity.getDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 15, entity.getDueDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 16, entity.getDatePaid(), OffsetDateTime.class);
                        bindOrNull(stmt, 17, entity.getDateRefunded(), OffsetDateTime.class);
                        bindOrNull(stmt, 18, entity.getDateCancelled(), OffsetDateTime.class);
                        bindOrNull(stmt, 19, entity.getCreatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 20, entity.getUpdatedAt(), OffsetDateTime.class);

                        if (index < invoices.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == invoices.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<Invoice> invoices) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE contacts
                    SET sub_total = $1,
                        total = $2,
                        credit = $3,
                        balance = $4,
                        tax = $5,
                        tax2 = $6,
                        tax_rate = $7,
                        tax_rate2 = $8,
                        status = $9,
                        currency = $10,
                        payment_method = $11,
                        items = $12,
                        due_date = $13,
                        date_paid = $14,
                        date_refunded = $15,
                        date_cancelled = $16,
                        updated_at = $17
                    WHERE id = $18
                    """);

            return Flux.fromIterable(invoices)
                    .map(invoiceMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getSubTotal(), String.class);
                        bindOrNull(stmt, 1, entity.getTotal(), String.class);
                        bindOrNull(stmt, 2, entity.getCredit(), String.class);
                        bindOrNull(stmt, 3, entity.getBalance(), String.class);
                        bindOrNull(stmt, 4, entity.getTax(), String.class);
                        bindOrNull(stmt, 5, entity.getTax2(), String.class);
                        bindOrNull(stmt, 6, entity.getTaxRate(), String.class);
                        bindOrNull(stmt, 7, entity.getTaxRate2(), String.class);
                        bindOrNull(stmt, 8, entity.getStatus(), String.class);
                        bindOrNull(stmt, 9, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 10, entity.getPaymentMethod(), String.class);
                        bindOrNull(stmt, 11, entity.getItems(), String.class);
                        bindOrNull(stmt, 12, entity.getDueDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 13, entity.getDatePaid(), OffsetDateTime.class);
                        bindOrNull(stmt, 14, entity.getDateRefunded(), OffsetDateTime.class);
                        bindOrNull(stmt, 15, entity.getDateCancelled(), OffsetDateTime.class);
                        bindOrNull(stmt, 16, entity.getUpdatedAt(), OffsetDateTime.class);
                        stmt.bind(17, entity.getId());

                        if (index < invoices.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == invoices.size());
        });
    }

    @Override
    public Flux<Invoice> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM invoices WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(InvoiceDb.class)
                .all()
                .map(invoiceMapper::map);
    }

    @Override
    public Mono<InvoiceSearchResult> findAllByFilter(InvoiceFilter filter, String cursor,
                                                     Integer offset, Integer limit) {
        var dbFilter = invoiceFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, InvoiceDb.class)
                .map(invoiceMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new InvoiceSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new InvoiceSearchResult(null, false, List.of())));
    }
}