package com.grunvald.sorm.infrastructure.persistence.data.paymentmethod;

import com.grunvald.sorm.infrastructure.persistence.data.paymentmethod.entity.PaymentMethodDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.paymentmethod.mapper.PaymentMethodFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.paymentmethod.model.PaymentMethod;
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

import java.time.OffsetDateTime;
import java.util.List;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.paymentMethodPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentMethodRepositoryImplTest {

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
    private PaymentMethodFilterPersistenceMapper paymentMethodFilterMapper;

    private PaymentMethodRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PaymentMethodRepositoryImpl(
                databaseClient, databaseClientAdapter, paymentMethodPersistenceMapper(), paymentMethodFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private PaymentMethod paymentMethod(String module) {
        var method = new PaymentMethod();
        method.setModule(module);
        method.setDisplayName("Display");
        method.setUpdatedAt(DATE);
        method.setDeleted(false);
        return method;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(paymentMethod("paypal"))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO payment_methods"));
        verify(statement).bind(0, "paypal");
        verify(statement).bind(1, "Display");
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var method = paymentMethod("paypal");
        method.setDisplayName(null);

        StepVerifier.create(repository.addAll(List.of(method)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(1, String.class);
    }

    @Test
    void updateAll_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAll(List.of(paymentMethod("paypal"), paymentMethod("stripe"))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE payment_methods"));
        verify(statement).add();
        verify(statement, times(2)).bind(0, "Display");
    }

    @Test
    void findAll_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<PaymentMethodDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(PaymentMethodDb.class)).thenReturn(rows);

        var db = new PaymentMethodDb();
        db.setId(1L);
        db.setModule("paypal");
        db.setDisplayName("Display");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAll())
                .assertNext(method -> {
                    assertEquals(1L, method.getId());
                    assertEquals("paypal", method.getModule());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM payment_methods"));
    }
}
