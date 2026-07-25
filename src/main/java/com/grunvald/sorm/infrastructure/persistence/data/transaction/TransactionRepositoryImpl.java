package com.grunvald.sorm.infrastructure.persistence.data.transaction;

import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.TransactionRepository;
import com.grunvald.sorm.service.whmcsdata.filter.TransactionFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.TransactionDb;
import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.mapper.TransactionPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.mapper.TransactionFilterPersistenceMapper;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.AllArgsConstructor;
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

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@AllArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final TransactionPersistenceMapper transactionMapper;
    private final TransactionFilterPersistenceMapper transactionFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Transaction> transactions) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO transactions
                    (id, invoice_id, client_id, trans_id, refund_id, currency, gateway, description, amount_in,
                    amount_out, fees, rate, date)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13)
                    """);

            return Flux.fromIterable(transactions)
                    .map(transactionMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getInvoiceId(), Long.class);
                        bindOrNull(stmt, 2, entity.getClientId(), Long.class);
                        bindOrNull(stmt, 3, entity.getTransId(), String.class);
                        bindOrNull(stmt, 4, entity.getRefundId(), Long.class);
                        bindOrNull(stmt, 5, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 6, entity.getGateway(), String.class);
                        bindOrNull(stmt, 7, entity.getDescription(), String.class);
                        bindOrNull(stmt, 8, entity.getAmountIn(), BigDecimal.class);
                        bindOrNull(stmt, 9, entity.getAmountOut(), BigDecimal.class);
                        bindOrNull(stmt, 10, entity.getFees(), BigDecimal.class);
                        bindOrNull(stmt, 11, entity.getRate(), BigDecimal.class);
                        bindOrNull(stmt, 12, entity.getDate(), OffsetDateTime.class);

                        if (index < transactions.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == transactions.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<Transaction> transactions) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE transactions
                    SET trans_id = $1,
                        refund_id = $2,
                        currency = $3,
                        gateway = $4,
                        description = $5,
                        amount_in = $6,
                        amount_out = $7,
                        fees = $8,
                        rate = $9,
                    WHERE id = $10
                    """);

            return Flux.fromIterable(transactions)
                    .map(transactionMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getTransId(), String.class);
                        bindOrNull(stmt, 1, entity.getRefundId(), Long.class);
                        bindOrNull(stmt, 2, entity.getCurrency(), String.class);
                        bindOrNull(stmt, 3, entity.getGateway(), String.class);
                        bindOrNull(stmt, 4, entity.getDescription(), String.class);
                        bindOrNull(stmt, 5, entity.getAmountIn(), BigDecimal.class);
                        bindOrNull(stmt, 6, entity.getAmountOut(), BigDecimal.class);
                        bindOrNull(stmt, 7, entity.getFees(), BigDecimal.class);
                        bindOrNull(stmt, 8, entity.getRate(), BigDecimal.class);
                        stmt.bind(9, entity.getId());


                        if (index < transactions.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == transactions.size());
        });
    }

    @Override
    public Flux<Transaction> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM users WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(TransactionDb.class)
                .all()
                .map(transactionMapper::map);
    }

    @Override
    public Mono<TransactionSearchResult> findAllByFilter(TransactionFilter filter, String cursor, Integer offset,
                                                         Integer limit) {
        var dbFilter = transactionFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, TransactionDb.class)
                .map(transactionMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var transactionList = hasNextPage ? list.subList(0, limit) : list;
                    var lastTransaction = transactionList.getLast();
                    var nextCursor = lastTransaction.getId().toString();
                    return new TransactionSearchResult(nextCursor, hasNextPage, transactionList);
                })
                .switchIfEmpty(Mono.just(new TransactionSearchResult(null, false, List.of())));
    }
}