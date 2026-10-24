package com.grunvald.sorm.infrastructure.persistence.data.activitylog;

import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.activitylog.mapper.ActivityLogFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.BiFunction;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.activityLogPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogRepositoryImplTest {

    private static final OffsetDateTime DATE = OffsetDateTime.parse("2026-01-02T03:04:05Z");

    @Mock
    private DatabaseClient databaseClient;
    @Mock
    private DatabaseClientAdapter databaseClientAdapter;
    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private ActivityLogFilterPersistenceMapper activityLogFilterMapper;

    private ActivityLogRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ActivityLogRepositoryImpl(
                databaseClient, databaseClientAdapter, activityLogPersistenceMapper(), activityLogFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private ActivityLog activityLog(Long id) {
        var log = new ActivityLog();
        log.setId(id);
        log.setClientId(10L);
        log.setUserId(20L);
        log.setDescription("login");
        log.setUsername("user");
        log.setIpAddress("127.0.0.1");
        log.setDate(DATE);
        return log;
    }

    @Test
    void getLatestOffsetDateTime_executesSelectAndReturnsDate() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<OffsetDateTime> rows = mock(RowsFetchSpec.class);
        when(spec.map(ArgumentMatchers.<BiFunction<Row, RowMetadata, OffsetDateTime>>any())).thenReturn(rows);
        when(rows.one()).thenReturn(Mono.just(DATE));

        StepVerifier.create(repository.getLatestOffsetDateTime())
                .expectNext(DATE)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM activity_logs"));
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(activityLog(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO activity_logs"));
        assertTrue(sqlCaptor.getValue().contains("user_id"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, 20L);
        verify(statement).bind(3, "login");
        verify(statement).bind(6, DATE);
    }

    @Test
    void addAll_multipleRowsChainsStatementsViaAdd() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAll(List.of(activityLog(1L), activityLog(2L))))
                .expectNext(true)
                .verifyComplete();

        verify(statement).add();
        verify(statement).bind(0, 1L);
        verify(statement).bind(0, 2L);
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var log = activityLog(1L);
        log.setUserId(null);
        log.setDescription(null);

        StepVerifier.create(repository.addAll(List.of(log)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(3, String.class);
    }

    @Test
    void addAll_returnsFalseWhenRowsUpdatedDoesNotMatch() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(activityLog(1L), activityLog(2L))))
                .expectNext(false)
                .verifyComplete();
    }
}
