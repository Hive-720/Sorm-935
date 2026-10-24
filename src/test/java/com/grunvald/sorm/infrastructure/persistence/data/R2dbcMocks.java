package com.grunvald.sorm.infrastructure.persistence.data;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Statement;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Общие помощники для мокирования R2DBC-взаимодействий в юнит-тестах репозиториев:
 * {@code databaseClient.inConnection(...)} и цепочек {@code Statement -> Result}.
 */
public final class R2dbcMocks {

    private R2dbcMocks() {
    }

    /**
     * Заставляет {@code databaseClient.inConnection(...)} немедленно выполнить переданное
     * действие против переданного мока {@link Connection}.
     */
    @SuppressWarnings("unchecked")
    public static void mockInConnection(DatabaseClient databaseClient, Connection connection) {
        lenient().when(databaseClient.inConnection(any()))
                .thenAnswer(invocation -> {
                    Function<Connection, Mono<Boolean>> action = invocation.getArgument(0);
                    return action.apply(connection);
                });
    }

    /**
     * {@code statement.execute()} вернёт цепочку результатов, каждый из которых сообщает
     * о соответствующем числе обновлённых строк.
     */
    public static void mockStatementExecution(Statement statement, long... rowsUpdated) {
        List<Result> results = new ArrayList<>();
        for (long rows : rowsUpdated) {
            Result result = mock(Result.class);
            when(result.getRowsUpdated()).thenReturn(Mono.just(rows));
            results.add(result);
        }
        // doReturn обходит проверку типов Mockito: Statement.execute() объявлен как
        // Publisher<? extends Result>, и Flux<Result> не подходит thenReturn из-за инвариантности.
        doReturn(Flux.fromIterable(results)).when(statement).execute();
    }
}
