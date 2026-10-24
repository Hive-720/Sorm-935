package com.grunvald.sorm.infrastructure.persistence.data.transaction;

import com.grunvald.sorm.infrastructure.persistence.data.transaction.entity.TransactionDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.transaction.mapper.TransactionFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.transactionPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

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
    private TransactionFilterPersistenceMapper transactionFilterMapper;

    private TransactionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepositoryImpl(
                databaseClient, databaseClientAdapter, transactionPersistenceMapper(), transactionFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Transaction transaction(Long id) {
        var transaction = new Transaction();
        transaction.setId(id);
        transaction.setInvoiceId(10L);
        transaction.setClientId(20L);
        transaction.setTransId("T-1");
        transaction.setRefundId(30L);
        transaction.setCurrency("USD");
        transaction.setGateway("paypal");
        transaction.setDescription("payment");
        transaction.setAmountIn(new BigDecimal("100.00"));
        transaction.setAmountOut(new BigDecimal("0.00"));
        transaction.setFees(new BigDecimal("2.00"));
        transaction.setRate(new BigDecimal("1.000000"));
        transaction.setDate(DATE);
        return transaction;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(transaction(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO transactions"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, 20L);
        verify(statement).bind(3, "T-1");
        verify(statement).bind(8, new BigDecimal("100.00"));
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var transaction = transaction(1L);
        transaction.setInvoiceId(null);
        transaction.setRefundId(null);

        StepVerifier.create(repository.addAll(List.of(transaction)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(1, Long.class);
        verify(statement).bindNull(4, Long.class);
    }

    @Test
    void updateAll_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAll(List.of(transaction(1L), transaction(2L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        var sql = sqlCaptor.getValue();
        assertTrue(sql.contains("UPDATE transactions"));
        assertTrue(sql.contains("rate = $9"));
        assertFalse(sql.contains("$9,"));
        verify(statement).add();
        verify(statement, times(2)).bind(0, "T-1");
        verify(statement).bind(9, 1L);
        verify(statement).bind(9, 2L);
    }

    @Test
    void findAllByIdIn_selectsFromTransactionsTableAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<TransactionDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(TransactionDb.class)).thenReturn(rows);

        var db = new TransactionDb();
        db.setId(1L);
        db.setClientId(20L);
        db.setTransId("T-1");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(transaction -> {
                    assertEquals(1L, transaction.getId());
                    assertEquals("T-1", transaction.getTransId());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM transactions"));
        verify(spec).bind("ids", Set.of(1L));
    }
}
