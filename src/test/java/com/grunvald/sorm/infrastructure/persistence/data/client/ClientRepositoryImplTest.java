package com.grunvald.sorm.infrastructure.persistence.data.client;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientGroupDb;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientLoginDb;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientProductDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientFilterPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientGroupFilterPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientLoginFilterPersistenceMapper;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper.ClientProductFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.clientGroupPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.clientLoginMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.clientPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.clientProductPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
class ClientRepositoryImplTest {

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
    private ClientFilterPersistenceMapper clientFilterMapper;
    @Mock
    private ClientGroupFilterPersistenceMapper clientGroupFilterMapper;
    @Mock
    private ClientProductFilterPersistenceMapper clientProductFilterMapper;
    @Mock
    private ClientLoginFilterPersistenceMapper clientLoginFilterMapper;

    private ClientRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ClientRepositoryImpl(
                databaseClient, databaseClientAdapter,
                clientPersistenceMapper(), clientGroupPersistenceMapper(), clientProductPersistenceMapper(),
                clientFilterMapper, clientGroupFilterMapper, clientProductFilterMapper, clientLoginFilterMapper,
                clientLoginMapper());
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Client client(Long id) {
        var client = new Client();
        client.setId(id);
        client.setClientGroupId(1L);
        client.setOwnerUserId(2L);
        client.setFullName("Full Name");
        client.setFirstName("First");
        client.setLastName("Last");
        client.setEmail("client@example.com");
        client.setPhonecc(7);
        client.setCredit(new BigDecimal("1.00"));
        client.setTaxExempt(true);
        client.setDateCreated(DATE);
        client.setDeleted(false);
        return client;
    }

    private ClientGroup clientGroup(Long id) {
        var group = new ClientGroup("group", new BigDecimal("5.0"));
        group.setId(id);
        group.setUpdatedAt(DATE);
        return group;
    }

    private ClientProduct clientProduct(Long id) {
        var product = new ClientProduct();
        product.setId(id);
        product.setClientId(10L);
        product.setProductId(20L);
        product.setOrderId(30L);
        product.setName("hosting");
        product.setDomain("example.com");
        product.setFirstPaymentAmount(new BigDecimal("10.00"));
        product.setRecurringAmount(new BigDecimal("5.00"));
        product.setBillingCycle("Monthly");
        product.setStatus("Active");
        product.setAssignedIpAddresses(List.of("1.2.3.4"));
        product.setRegDate(DATE);
        product.setNextDueDate(DATE);
        product.setLastUpdate(DATE);
        return product;
    }

    @Test
    void findAllClientGroup_selectsAllAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        RowsFetchSpec<ClientGroupDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ClientGroupDb.class)).thenReturn(rows);

        var db = new ClientGroupDb();
        db.setId(1L);
        db.setName("group");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllClientGroup())
                .assertNext(group -> {
                    assertEquals(1L, group.getId());
                    assertEquals("group", group.getName());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM client_groups"));
    }

    @Test
    void addAllClient_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllClient(List.of(client(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO clients"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 1L);
        verify(statement).bind(3, "Full Name");
        verify(statement).bind(14, 7);
        verify(statement).bind(22, new BigDecimal("1.00"));
    }

    @Test
    void addAllClient_multipleRowsChainsStatementsViaAdd() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAllClient(List.of(client(1L), client(2L))))
                .expectNext(true)
                .verifyComplete();

        verify(statement).add();
        verify(statement).bind(0, 2L);
    }

    @Test
    void addAllClient_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var client = client(1L);
        client.setClientGroupId(null);
        client.setOwnerUserId(null);

        StepVerifier.create(repository.addAllClient(List.of(client)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(1, Long.class);
        verify(statement).bindNull(2, Long.class);
    }

    @Test
    void addAllClientGroup_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllClientGroup(List.of(clientGroup(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO client_groups"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, "group");
        verify(statement).bind(2, new BigDecimal("5.0"));
    }

    @Test
    void updateAllClientGroup_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllClientGroup(List.of(clientGroup(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE client_groups"));
        verify(statement).bind(0, "group");
        verify(statement).bind(4, 1L);
    }

    @Test
    void softDeleteAllClient_returnsTrueWhenRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(2L));

        StepVerifier.create(repository.softDeleteAllClient(Set.of(1L, 2L)))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE clients"));
        assertTrue(sqlCaptor.getValue().contains("deleted = true"));
    }

    @Test
    void softDeleteAllClient_returnsFalseWhenNoRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(0L));

        StepVerifier.create(repository.softDeleteAllClient(Set.of(3L)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void addAllClientProduct_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllClientProduct(List.of(clientProduct(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO client_products"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        ArgumentCaptor<Object> ipsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(10), ipsCaptor.capture());
        assertTrue(ipsCaptor.getValue() instanceof String[] ips && ips.length == 1 && "1.2.3.4".equals(ips[0]));
    }

    @Test
    void addAllClientProduct_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var product = clientProduct(1L);
        product.setProductId(null);
        product.setConfigs(null);

        StepVerifier.create(repository.addAllClientProduct(List.of(product)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, Long.class);
        verify(statement).bindNull(12, io.r2dbc.postgresql.codec.Json.class);
    }

    @Test
    void updateAllClientProduct_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllClientProduct(List.of(clientProduct(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE client_products"));
        verify(statement).bind(0, "hosting");
        verify(statement).bind(11, 1L);
    }

    @Test
    void findAllClientLastLoginByClientIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ClientLoginDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ClientLoginDb.class)).thenReturn(rows);

        var db = new ClientLoginDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setIpAddress("127.0.0.1");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllClientLastLoginByClientIdIn(Set.of(10L)))
                .assertNext(login -> {
                    assertEquals(1L, login.getId());
                    assertEquals("127.0.0.1", login.getIpAddress());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("client_logins"));
    }

    @Test
    void findAllClientProductByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ClientProductDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ClientProductDb.class)).thenReturn(rows);

        var db = new ClientProductDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setAssignedIps(new String[]{"1.2.3.4"});
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllClientProductByIdIn(Set.of(1L)))
                .assertNext(product -> assertEquals(List.of("1.2.3.4"), product.getAssignedIpAddresses()))
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void updateAllClient_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAllClient(List.of(client(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE clients"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(3, "First");
        verify(statement).bind(25, 1L);
    }

    @Test
    void addAllClientLogin_bindsValuesAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAllClientLogin(
                        List.of(new ClientLogin(10L, "127.0.0.1", DATE))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO client_logins"));
        verify(statement).bind(0, 10L);
        verify(statement).bind(1, "127.0.0.1");
        verify(statement).bind(2, DATE);
    }

    @Test
    void findAllClientByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ClientDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ClientDb.class)).thenReturn(rows);

        var db = new ClientDb();
        db.setId(1L);
        db.setFullName("Full Name");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllClientByIdIn(Set.of(1L)))
                .assertNext(client -> assertEquals("Full Name", client.getFullName()))
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM clients"));
    }

    @Test
    void findAll_withoutCursor_selectsIdsUpToLimit() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bindValues(ArgumentMatchers.<List<Object>>any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        var row = Map.<String, Object>of("id", 1L);
        when(fetch.all()).thenReturn(Flux.just(row));

        StepVerifier.create(repository.findAll(null, 2))
                .assertNext(result -> {
                    assertEquals(1L, result.cursorById());
                    assertFalse(result.hasNextPage());
                    assertEquals(List.of(1L), result.clientIds());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertFalse(sqlCaptor.getValue().contains("WHERE id"));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Object>> paramsCaptor = ArgumentCaptor.forClass(List.class);
        verify(spec).bindValues(paramsCaptor.capture());
        assertEquals(List.of(3), paramsCaptor.getValue());
    }

    @Test
    void findAll_withCursor_hasNextPageWhenMoreRowsThanLimit() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bindValues(ArgumentMatchers.<List<Object>>any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.all()).thenReturn(Flux.just(
                Map.<String, Object>of("id", 2L),
                Map.<String, Object>of("id", 3L),
                Map.<String, Object>of("id", 4L)));

        StepVerifier.create(repository.findAll(1L, 2))
                .assertNext(result -> {
                    assertEquals(3L, result.cursorById());
                    assertTrue(result.hasNextPage());
                    assertEquals(List.of(2L, 3L), result.clientIds());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("WHERE id > :id"));
    }

    @Test
    void findAllClientGroupByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ClientGroupDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ClientGroupDb.class)).thenReturn(rows);

        var db = new ClientGroupDb();
        db.setId(1L);
        db.setName("group");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllClientGroupByIdIn(Set.of(1L)))
                .assertNext(group -> assertEquals("group", group.getName()))
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }
}
