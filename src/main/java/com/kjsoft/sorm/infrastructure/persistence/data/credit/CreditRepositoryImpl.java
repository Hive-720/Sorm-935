package com.kjsoft.sorm.infrastructure.persistence.data.credit;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.CreditRepository;
import com.kjsoft.sorm.service.whmcsdata.filter.CreditFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import com.kjsoft.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import com.kjsoft.sorm.infrastructure.persistence.data.credit.entity.mapper.CreditPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.credit.mapper.CreditFilterPersistenceMapper;
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
public class CreditRepositoryImpl implements CreditRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final CreditPersistenceMapper creditMapper;
    private final CreditFilterPersistenceMapper creditFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Credit> creditList) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO credits
                    (id, client_id, rel_id, description, amount, date)
                    VALUES ($1, $2, $3, $4, $5, $6)
                    """);

            return Flux.fromIterable(creditList)
                    .map(creditMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getRelId(), BigDecimal.class);
                        bindOrNull(stmt, 3, entity.getDescription(), OffsetDateTime.class);
                        bindOrNull(stmt, 4, entity.getAmount(), OffsetDateTime.class);
                        bindOrNull(stmt, 5, entity.getDate(), OffsetDateTime.class);

                        if (index < creditList.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == creditList.size());
        });
    }

    @Override
    public Flux<Credit> findLatestAllByClientId(Set<Long> ids) {
        return databaseClient.sql("""
                        SELECT
                            cr.*
                        FROM (
                            SELECT
                                *,
                                ROW_NUMBER() OVER (PARTITION BY client_id ORDER BY date DESC) as rn
                            FROM
                                credits
                            WHERE
                                client_id IN (:ids)
                        ) AS cr
                        WHERE
                            cr.rn = 1
                        """)
                .bind("ids", ids)
                .mapProperties(CreditDb.class)
                .all()
                .map(creditMapper::map);
    }

    @Override
    public Flux<Credit> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM credits WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(CreditDb.class)
                .all()
                .map(creditMapper::map);
    }

    @Override
    public Mono<CreditSearchResult> findAllByFilter(CreditFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = creditFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, CreditDb.class)
                .map(creditMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new CreditSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new CreditSearchResult(null, false, List.of())));
    }
}