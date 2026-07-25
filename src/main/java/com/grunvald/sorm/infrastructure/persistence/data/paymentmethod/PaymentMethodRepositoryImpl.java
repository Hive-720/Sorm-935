package com.grunvald.sorm.infrastructure.persistence.data.paymentmethod;

import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.PaymentMethodRepository;
import com.grunvald.sorm.service.whmcsdata.filter.PaymentMethodFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.dto.PaymentMethodSearchResult;
import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.PaymentMethodDb;
import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.mapper.PaymentMethodPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.mapper.PaymentMethodFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class PaymentMethodRepositoryImpl implements PaymentMethodRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final PaymentMethodPersistenceMapper paymentMethodMapper;
    private final PaymentMethodFilterPersistenceMapper paymentMethodFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<PaymentMethod> paymentMethods) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement(""" 
                    INSERT INTO payment_methods
                    (module, display_name, updated_at, deleted)
                    VALUES ($1, $2, $3, $4)
                    """);

            return Flux.fromIterable(paymentMethods)
                    .map(paymentMethodMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getModule());
                        bindOrNull(stmt, 1, entity.getDisplayName(), String.class);
                        bindOrNull(stmt, 2, entity.getUpdatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 3, entity.isDeleted(), Boolean.class);

                        if (index < paymentMethods.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == paymentMethods.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<PaymentMethod> paymentMethods) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE payment_methods
                    SET display_name = $1,
                        updated_at = $2,
                        deleted = $3
                    WHERE id = $4
                    """);

            return Flux.fromIterable(paymentMethods)
                    .map(paymentMethodMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getDisplayName(), String.class);
                        bindOrNull(stmt, 1, entity.getUpdatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 2, entity.isDeleted(), Boolean.class);
                        stmt.bind(3, entity.getId());

                        if (index < paymentMethods.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == paymentMethods.size());
        });
    }

    @Override
    public Flux<PaymentMethod> findAll() {
        return databaseClient.sql("SELECT * FROM payment_methods")
                .mapProperties(PaymentMethodDb.class)
                .all()
                .map(paymentMethodMapper::map);
    }

    @Override
    public Mono<PaymentMethodSearchResult> findAllByFilter(PaymentMethodFilter filter, String cursor,
                                                           Integer offset, Integer limit) {
        var dbFilter = paymentMethodFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, PaymentMethodDb.class)
                .map(paymentMethodMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new PaymentMethodSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new PaymentMethodSearchResult(null, false, List.of())));
    }
}