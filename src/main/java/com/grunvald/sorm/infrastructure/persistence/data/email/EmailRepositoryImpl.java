package com.grunvald.sorm.infrastructure.persistence.data.email;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.EmailRepository;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import com.grunvald.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import com.grunvald.sorm.infrastructure.persistence.data.email.entity.mapper.EmailPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.email.mapper.EmailFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class EmailRepositoryImpl implements EmailRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final EmailPersistenceMapper emailMapper;
    private final EmailFilterPersistenceMapper emailFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> addAll(List<Email> emails) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO emails
                    (id, client_id, subject, message_body, to, cc, bcc, date, created_at, updated_at)
                    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
                    """);

            return Flux.fromIterable(emails)
                    .map(emailMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getSubject(), String.class);
                        bindOrNull(stmt, 3, entity.getMessageBody(), String.class);
                        bindOrNull(stmt, 4, entity.getTo(), String.class);
                        bindOrNull(stmt, 5, entity.getCc(), String[].class);
                        bindOrNull(stmt, 6, entity.getBcc(), String[].class);
                        bindOrNull(stmt, 7, entity.getDate(), OffsetDateTime.class);
                        bindOrNull(stmt, 8, entity.getCreatedAt(), OffsetDateTime.class);
                        bindOrNull(stmt, 9, entity.getUpdatedAt(), OffsetDateTime.class);

                        if (index < emails.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == emails.size());
        });
    }

    @Override
    public Flux<Email> getLatestAllByClientId(Set<Long> ids) {
        return databaseClient.sql("""
                        SELECT
                            eml.*
                        FROM (
                            SELECT
                                *,
                                ROW_NUMBER() OVER (PARTITION BY client_id ORDER BY date DESC) as rn
                            FROM
                                emails
                            WHERE
                                client_id IN (:ids)
                        ) AS eml
                        WHERE
                            eml.rn = 1
                        """)
                .bind("ids", ids)
                .mapProperties(EmailDb.class)
                .all()
                .map(emailMapper::map);
    }

    @Override
    public Mono<EmailSearchResult> findAllByFilter(EmailFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = emailFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, EmailDb.class)
                .map(emailMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new EmailSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new EmailSearchResult(null, false, List.of())));
    }
}