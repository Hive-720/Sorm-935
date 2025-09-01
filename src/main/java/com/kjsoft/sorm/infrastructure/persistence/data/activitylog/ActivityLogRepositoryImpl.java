package com.kjsoft.sorm.infrastructure.persistence.data.activitylog;

import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.ActivityLogRepository;
import com.kjsoft.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.kjsoft.sorm.infrastructure.persistence.data.activitylog.entity.ActivityLogDb;
import com.kjsoft.sorm.infrastructure.persistence.data.activitylog.entity.mapper.ActivityLogPersistenceMapper;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.mapper.ActivityLogFilterPersistenceMapper;
import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
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

import static com.kjsoft.sorm.infrastructure.persistence.util.PersistenceHelper.bindOrNull;


@Repository
@RequiredArgsConstructor
public class ActivityLogRepositoryImpl implements ActivityLogRepository {
    private final DatabaseClient databaseClient;
    private final DatabaseClientAdapter databaseClientAdapter;
    private final ActivityLogPersistenceMapper activityLogMapper;
    private final ActivityLogFilterPersistenceMapper activityLogFilterMapper;


    @Override
    public Mono<OffsetDateTime> getLatestOffsetDateTime() {
        return databaseClient.sql("""
                        SELECT date
                        FROM activity_logs
                        ORDER BY date DESC
                        LIMIT 1
                        """)
                .map((row, metadata) -> row.get("date", OffsetDateTime.class))
                .one();
    }

    @Override
    @Transactional
    public Mono<Boolean> addAll(List<ActivityLog> activityLogs) {
        return databaseClient.inConnection(connection -> {
            var statement = connection.createStatement("""
                    INSERT INTO activity_logs
                    (id, client_id, user_id, date, description, username, ip_address)
                    VALUES ($1, $2, $3, $4, $5, $6, $7)
                    """);

            return Flux.fromIterable(activityLogs)
                    .map(activityLogMapper::map)
                    .index()
                    .reduce(statement, (stmt, tuple) -> {
                        var index = tuple.getT1();
                        var entity = tuple.getT2();

                        stmt.bind(0, entity.getId());
                        stmt.bind(1, entity.getClientId());
                        bindOrNull(stmt, 2, entity.getUserId(), Long.class);
                        bindOrNull(stmt, 3, entity.getDescription(), String.class);
                        bindOrNull(stmt, 4, entity.getUsername(), String.class);
                        bindOrNull(stmt, 5, entity.getIpAddress(), String.class);
                        bindOrNull(stmt, 6, entity.getDate(), OffsetDateTime.class);

                        if (index < activityLogs.size() - 1) {
                            return stmt.add();
                        }
                        return stmt;
                    })
                    .flatMapMany(Statement::execute)
                    .flatMap(Result::getRowsUpdated)
                    .collect(Collectors.summingLong(Number::longValue))
                    .map(totalRows -> totalRows == activityLogs.size());
        });
    }

    @Override
    public Mono<ActivityLogSearchResult> findAllByFilter(ActivityLogFilter filter, String cursor,
                                                         Integer offset, Integer limit) {
        var dbFilter = activityLogFilterMapper.map(filter);
        var limitPlusOne = limit + 1;

        return databaseClientAdapter.all(dbFilter, cursor, offset, limitPlusOne, ActivityLogDb.class)
                .map(activityLogMapper::map)
                .collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> {
                    var hasNextPage = list.size() > limit;
                    var resultList = hasNextPage ? list.subList(0, limit) : list;
                    var lastUser = resultList.getLast();
                    var nextCursor = lastUser.getId().toString();
                    return new ActivityLogSearchResult(nextCursor, hasNextPage, resultList);
                })
                .switchIfEmpty(Mono.just(new ActivityLogSearchResult(null, false, List.of())));
    }
}