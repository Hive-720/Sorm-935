package com.grunvald.sorm.infrastructure.persistence.data.invoice;

import com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.InvoiceDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.invoice.mapper.InvoiceFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import io.r2dbc.postgresql.codec.Json;
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
import static com.grunvald.sorm.testutil.MapStructTestFactory.invoicePersistenceMapper;
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
class InvoiceRepositoryImplTest {

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
    private InvoiceFilterPersistenceMapper invoiceFilterMapper;

    private InvoiceRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new InvoiceRepositoryImpl(
                databaseClient, databaseClientAdapter, invoicePersistenceMapper(), invoiceFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Invoice invoice(Long id) {
        var invoice = new Invoice();
        invoice.setId(id);
        invoice.setClientId(10L);
        invoice.setSubTotal(new BigDecimal("10.00"));
        invoice.setTotal(new BigDecimal("11.00"));
        invoice.setCredit(new BigDecimal("0.00"));
        invoice.setBalance(new BigDecimal("11.00"));
        invoice.setStatus("Paid");
        invoice.setCurrency("USD");
        invoice.setDate(DATE);
        invoice.setDueDate(DATE);
        invoice.setCreatedAt(DATE);
        invoice.setUpdatedAt(DATE);
        return invoice;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(invoice(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO invoices"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, new BigDecimal("10.00"));
        verify(statement).bind(10, "Paid");
        // items не заданы — null-JSON
        verify(statement).bindNull(13, Json.class);
    }

    @Test
    void addAll_multipleRowsChainsStatementsViaAdd() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAll(List.of(invoice(1L), invoice(2L))))
                .expectNext(true)
                .verifyComplete();

        verify(statement).add();
        verify(statement).bind(0, 2L);
    }

    @Test
    void updateAll_bindsValuesExecutesUpdateAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAll(List.of(invoice(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE invoices"));
        verify(statement).bind(0, new BigDecimal("10.00"));
        verify(statement).bind(8, "Paid");
        verify(statement).bind(17, 1L);
    }

    @Test
    void findAllByIdIn_bindsIdsAndMapsRowsWithJsonItems() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<InvoiceDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(InvoiceDb.class)).thenReturn(rows);

        var db = new InvoiceDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setStatus("Paid");
        db.setItems(Json.of("[{\"id\":1,\"relId\":2,\"type\":\"Setup\",\"description\":\"setup fee\",\"amount\":10.00,\"taxed\":false}]"));
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(invoice -> {
                    assertEquals(1L, invoice.getId());
                    assertEquals("Paid", invoice.getStatus());
                    assertEquals(1, invoice.getItems().size());
                    assertEquals("Setup", invoice.getItems().getFirst().getType());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM invoices"));
        verify(spec).bind("ids", Set.of(1L));
    }
}
