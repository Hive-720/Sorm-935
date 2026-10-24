package com.grunvald.sorm.infrastructure.persistence.data.credit;

import com.grunvald.sorm.infrastructure.persistence.data.credit.entity.CreditDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.credit.mapper.CreditFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
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
import static com.grunvald.sorm.testutil.MapStructTestFactory.creditPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditRepositoryImplTest {

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
    private CreditFilterPersistenceMapper creditFilterMapper;

    private CreditRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CreditRepositoryImpl(
                databaseClient, databaseClientAdapter, creditPersistenceMapper(), creditFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Credit credit(Long id) {
        var credit = new Credit();
        credit.setId(id);
        credit.setClientId(10L);
        credit.setRelId(20L);
        credit.setDescription("refund");
        credit.setAmount(new BigDecimal("15.50"));
        credit.setDate(DATE);
        return credit;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(credit(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO credits"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, 20L);
        verify(statement).bind(3, "refund");
        verify(statement).bind(4, new BigDecimal("15.50"));
    }

    @Test
    void addAll_bindsNullWithCorrectTypes() {
        mockStatementExecution(statement, 1L);
        var credit = credit(1L);
        credit.setRelId(null);
        credit.setDescription(null);
        credit.setAmount(null);

        StepVerifier.create(repository.addAll(List.of(credit)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(3, String.class);
        verify(statement).bindNull(4, BigDecimal.class);
    }

    @Test
    void addAll_multipleRowsChainsStatementsViaAdd() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAll(List.of(credit(1L), credit(2L))))
                .expectNext(true)
                .verifyComplete();

        verify(statement).add();
        verify(statement).bind(0, 2L);
    }

    @Test
    void findLatestAllByClientId_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<CreditDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(CreditDb.class)).thenReturn(rows);

        var db = new CreditDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setDescription("refund");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findLatestAllByClientId(Set.of(10L)))
                .assertNext(credit -> {
                    assertEquals(1L, credit.getId());
                    assertEquals("refund", credit.getDescription());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("credits"));
        assertTrue(sqlCaptor.getValue().contains("ROW_NUMBER"));
    }

    @Test
    void findAllByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<CreditDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(CreditDb.class)).thenReturn(rows);

        var db = new CreditDb();
        db.setId(1L);
        db.setAmount(new BigDecimal("15.50"));
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(credit -> assertEquals(new BigDecimal("15.50"), credit.getAmount()))
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }
}
