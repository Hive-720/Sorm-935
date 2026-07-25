package com.grunvald.sorm.infrastructure.persistence.data.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.AffiliateRepository;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.AffiliateDb;
import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.mapper.AffiliatePersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.mapper.AffiliateFilterPersistenceMapper;
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

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class AffiliateRepositoryImpl implements AffiliateRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final AffiliatePersistenceMapper affiliateMapper;
    private final AffiliateFilterPersistenceMapper affiliateFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Affiliate> affiliates) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO affiliates
                    (id, client_id, visitors, pay_type, pay_amount, one_time, balance, withdrawn, date, created_at, updated_at)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
                    """);

            return Flux.fromIterable(affiliates)
                    .map(affiliateMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getVisitors(), Long.class);
                        bindOrNull(stmt, 3, entity.getPayType(), String.class);
                        bindOrNull(stmt, 4, entity.getPayAmount(), BigDecimal.class);
                        bindOrNull(stmt, 5, entity.getOneTime(), Boolean.class);
                        bindOrNull(stmt, 6, entity.getBalance(), BigDecimal.class);
                        bindOrNull(stmt, 7, entity.getWithdrawn(), BigDecimal.class);
                        bindOrNull(stmt, 8, entity.getDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 9, entity.getCreatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 10, entity.getUpdatedAt(), OffsetDateTime.class);

                        if (index < affiliates.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == affiliates.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<Affiliate> affiliates) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE affiliates
                    SET visitors = $1,
                        pay_type = $2,
                        pay_amount = $3,
                        one_time = $4,
                        balance = $5,
                        withdrawn = $6,
                        updated_at = $7
                    WHERE id = $8
                    """);

            return Flux.fromIterable(affiliates)
                    .map(affiliateMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getVisitors(), Long.class);
                        bindOrNull(stmt, 1, entity.getPayType(), String.class);
                        bindOrNull(stmt, 2, entity.getPayAmount(), BigDecimal.class);
                        bindOrNull(stmt, 3, entity.getOneTime(), Boolean.class);
                        bindOrNull(stmt, 4, entity.getBalance(), BigDecimal.class);
                        bindOrNull(stmt, 5, entity.getWithdrawn(), BigDecimal.class);
                        bindOrNull(stmt, 6, entity.getUpdatedAt(), OffsetDateTime.class);
                        stmt.bind(7, entity.getId());

                        if (index < affiliates.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == affiliates.size());
        });
    }

    @Override
    public Flux<Affiliate> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM affiliates WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(AffiliateDb.class)
                .all()
                .map(affiliateMapper::map);
    }

    @Override
    public Mono<AffiliateSearchResult> findAllAffiliateByFilter(AffiliateFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = affiliateFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, AffiliateDb.class)
                .map(affiliateMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new AffiliateSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new AffiliateSearchResult(null, false, List.of())));
    }
}