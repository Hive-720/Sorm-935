package com.grunvald.sorm.infrastructure.persistence.data.contact;

import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.ContactDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.mapper.ContactFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.FetchSpec;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.contactPersistenceMapper;
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
class ContactRepositoryImplTest {

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
    private ContactFilterPersistenceMapper contactFilterMapper;

    private ContactRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new ContactRepositoryImpl(
                databaseClient, databaseClientAdapter, contactPersistenceMapper(), contactFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Contact contact(Long id) {
        var contact = new Contact();
        contact.setId(id);
        contact.setClientId(10L);
        contact.setFullName("Full Name");
        contact.setFirstName("First");
        contact.setLastName("Last");
        contact.setEmail("contact@example.com");
        contact.setCreatedAt(DATE);
        contact.setUpdatedAt(DATE);
        return contact;
    }

    @Test
    void getAllIds_selectsIdsAndReturnsThem() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.all()).thenReturn(Flux.just(
                Map.of("id", 1L),
                Map.of("id", 2L)));

        StepVerifier.create(repository.getAllIds())
                .expectNext(1L)
                .expectNext(2L)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM contacts"));
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(contact(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO contacts"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, "Full Name");
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var contact = contact(1L);
        contact.setCompanyName(null);
        contact.setAddress(null);

        StepVerifier.create(repository.addAll(List.of(contact)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(5, String.class);
        verify(statement).bindNull(7, String.class);
    }

    @Test
    void updateAll_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.updateAll(List.of(contact(1L), contact(2L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE contacts"));
        verify(statement).add();
        verify(statement).bind(13, 1L);
        verify(statement).bind(13, 2L);
    }

    @Test
    void softDeleteAll_returnsTrueWhenRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(1L));

        StepVerifier.create(repository.softDeleteAll(Set.of(1L)))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE contacts"));
        assertTrue(sqlCaptor.getValue().contains("deleted = true"));
        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void softDeleteAll_returnsFalseWhenNoRowsUpdated() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        FetchSpec<Map<String, Object>> fetch = mock(FetchSpec.class);
        when(spec.fetch()).thenReturn(fetch);
        when(fetch.rowsUpdated()).thenReturn(Mono.just(0L));

        StepVerifier.create(repository.softDeleteAll(Set.of(1L)))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findAllByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<ContactDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(ContactDb.class)).thenReturn(rows);

        var db = new ContactDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setFullName("Full Name");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(contact -> {
                    assertEquals(1L, contact.getId());
                    assertEquals("Full Name", contact.getFullName());
                })
                .verifyComplete();

        verify(spec).bind("ids", Set.of(1L));
    }
}
