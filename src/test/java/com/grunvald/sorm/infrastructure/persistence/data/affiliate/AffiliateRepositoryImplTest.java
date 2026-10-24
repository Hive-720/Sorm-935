package com.grunvald.sorm.infrastructure.persistence.data.affiliate;

import com.grunvald.sorm.infrastructure.persistence.data.affiliate.entity.AffiliateDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.affiliate.mapper.AffiliateFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
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
import static com.grunvald.sorm.testutil.MapStructTestFactory.affiliatePersistenceMapper;
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
class AffiliateRepositoryImplTest {

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
    private AffiliateFilterPersistenceMapper affiliateFilterMapper;

    private AffiliateRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new AffiliateRepositoryImpl(
                databaseClient, databaseClientAdapter, affiliatePersistenceMapper(), affiliateFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Affiliate affiliate(Long id) {
        var affiliate = new Affiliate();
        affiliate.setId(id);
        affiliate.setClientId(10L);
        affiliate.setVisitors(100L);
        affiliate.setPayType("onetime");
        affiliate.setPayAmount(new BigDecimal("5.00"));
        affiliate.setOneTime(true);
        affiliate.setBalance(new BigDecimal("10.00"));
        affiliate.setWithdrawn(new BigDecimal("3.00"));
        affiliate.setDate(DATE);
        affiliate.setCreatedAt(DATE);
        affiliate.setUpdatedAt(DATE);
        return affiliate;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(affiliate(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO affiliates"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, 100L);
        verify(statement).bind(4, new BigDecimal("5.00"));
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var affiliate = affiliate(1L);
        affiliate.setVisitors(null);
        affiliate.setOneTime(null);

        StepVerifier.create(repository.addAll(List.of(affiliate)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(5, Boolean.class);
    }

    @Test
    void updateAll_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAll(List.of(affiliate(1L), affiliate(2L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE affiliates"));
        verify(statement).add();
        verify(statement).bind(7, 1L);
        verify(statement).bind(7, 2L);
    }

    @Test
    void findAllByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<AffiliateDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(AffiliateDb.class)).thenReturn(rows);

        var db = new AffiliateDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setPayType("recurring");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(affiliate -> {
                    assertEquals(1L, affiliate.getId());
                    assertEquals(10L, affiliate.getClientId());
                    assertEquals("recurring", affiliate.getPayType());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM affiliates"));
        verify(spec).bind("ids", Set.of(1L));
        verify(spec).mapProperties(AffiliateDb.class);
    }
}
