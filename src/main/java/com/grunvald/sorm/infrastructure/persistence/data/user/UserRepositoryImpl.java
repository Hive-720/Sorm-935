package com.grunvald.sorm.infrastructure.persistence.data.user;

import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserClientRelationPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.relation.UserClientRelationDb;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserRepository;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.UserDb;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper.UserPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.mapper.UserFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.grunvald.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final UserPersistenceMapper userMapper;
    private final UserClientRelationPersistenceMapper userClientRelationMapper;
    private final UserFilterPersistenceMapper userFilterMapper;


    @Override
    @Transactional
    public Mono<Boolean> deleteAllRelations(List<UserClientRelation> relations) {
        return databaseClient.inConnection(connection -> {
            var productIds = new ArrayList<Long>();
            var configIds = new ArrayList<Long>();

            for (var relation : relations) {
                productIds.add(relation.getUserId());
                configIds.add(relation.getClientId());
            }

            //TODO: проверить
            var sql = """
                    DELETE FROM client_user_relations
                    WHERE (user_id, client_id) IN (
                        SELECT unnest(?::bigint[]), unnest(?::bigint[])
                    )
                    """;

            var statement = connection.createStatement(sql)
                    .bind(0, productIds.toArray(new Long[0]))
                    .bind(1, configIds.toArray(new Long[0]));

            return Mono.from(statement.execute())
                    .flatMap(result -> Mono.from(result.getRowsUpdated()))
                    .map(rows -> rows > 0);
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> addAllRelations(List<UserClientRelation> relations) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO client_user_relations
                    (user_id, client_id, created_at)
                    VALUES ($1, $2, $3)
                    """);

            return Flux.fromIterable(relations)
                    .map(userClientRelationMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getUserId());
                        stmt.bind(1, entity.getClientId());
                        stmt.bind(2, entity.getCreatedAt());

                        if (index < relations.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == relations.size());
        });
    }

    @Override
    public Flux<UserClientRelation> findAllRelationsByUserIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM client_user_relations WHERE user_id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(UserClientRelationDb.class)
                .all()
                .map(userClientRelationMapper::map);
    }

    @Override
    @Transactional
    public Mono<Boolean> addAll(List<User> users) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO users
                    (id, first_name, last_name, full_name, email, date_created)
                    VALUES ($1, $2, $3, $4, $5, $6)
                    """);

            return Flux.fromIterable(users)
                    .map(userMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        bindOrNull(stmt, 1, entity.getFirstName(), String.class);
                        bindOrNull(stmt, 2, entity.getLastName(), String.class);
                        bindOrNull(stmt, 3, entity.getFullName(), String.class);
                        bindOrNull(stmt, 4, entity.getEmail(), String.class);
                        bindOrNull(stmt, 5, entity.getDateCreated(), OffsetDateTime.class);

                        if (index < users.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == users.size());
        });
    }

    @Override
    @Transactional
    public Mono<Boolean> updateAll(List<User> users) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    UPDATE users
                    SET first_name = $1,
                        last_name = $2,
                        full_name = $3,
                        email = $4
                    WHERE id = $5
                    """);

            return Flux.fromIterable(users)
                    .map(userMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        bindOrNull(stmt, 0, entity.getFirstName(), String.class);
                        bindOrNull(stmt, 1, entity.getLastName(), String.class);
                        bindOrNull(stmt, 2, entity.getFullName(), String.class);
                        bindOrNull(stmt, 3, entity.getEmail(), String.class);
                        bindOrNull(stmt, 4, entity.getId(), Long.class);

                        if (index < users.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == users.size());
        });
    }

    @Override
    public Flux<User> findAllByIdIn(Set<Long> ids) {
        return databaseClient.sql("SELECT * FROM users WHERE id IN(:ids)")
                .bind("ids", ids)
                .mapProperties(UserDb.class)
                .all()
                .map(userMapper::map);
    }

    @Override
    public Mono<UserSearchResult> findAllByFilter(UserFilter filter, String cursor, Integer offset, Integer limit) {
        var dbFilter = userFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, UserDb.class)
                .map(userMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new UserSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new UserSearchResult(null, false, List.of())));
    }
}