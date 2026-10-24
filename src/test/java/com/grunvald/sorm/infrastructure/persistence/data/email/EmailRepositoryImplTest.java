package com.grunvald.sorm.infrastructure.persistence.data.email;

import com.grunvald.sorm.infrastructure.persistence.data.email.entity.EmailDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.email.mapper.EmailFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
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
import java.util.Set;

import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockInConnection;
import static com.grunvald.sorm.infrastructure.persistence.data.R2dbcMocks.mockStatementExecution;
import static com.grunvald.sorm.testutil.MapStructTestFactory.emailPersistenceMapper;
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
class EmailRepositoryImplTest {

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
    private EmailFilterPersistenceMapper emailFilterMapper;

    private EmailRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new EmailRepositoryImpl(
                databaseClient, databaseClientAdapter, emailPersistenceMapper(), emailFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private Email email(Long id) {
        var email = new Email();
        email.setId(id);
        email.setClientId(10L);
        email.setSubject("Subject");
        email.setMessageBody("Body");
        email.setTo("to@example.com");
        email.setCc(List.of("cc@example.com"));
        email.setBcc(List.of("bcc@example.com"));
        email.setDate(DATE);
        email.setCreatedAt(DATE);
        email.setUpdatedAt(DATE);
        return email;
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(email(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO emails"));
        assertTrue(sqlCaptor.getValue().contains("\"to\""));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement).bind(2, "Subject");
        verify(statement).bind(4, "to@example.com");

        ArgumentCaptor<Object> ccCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(5), ccCaptor.capture());
        assertTrue(ccCaptor.getValue() instanceof String[] cc && cc.length == 1 && "cc@example.com".equals(cc[0]));
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var email = email(1L);
        email.setSubject(null);
        email.setCc(null);
        email.setBcc(null);

        StepVerifier.create(repository.addAll(List.of(email)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, String.class);
        verify(statement).bindNull(5, String[].class);
        verify(statement).bindNull(6, String[].class);
    }

    @Test
    void addAll_multipleRowsChainsStatementsViaAdd() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAll(List.of(email(1L), email(2L))))
                .expectNext(true)
                .verifyComplete();

        verify(statement).add();
        verify(statement).bind(0, 2L);
    }

    @Test
    void addAll_returnsFalseWhenRowsUpdatedDoesNotMatch() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(email(1L), email(2L))))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getLatestAllByClientId_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<EmailDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(EmailDb.class)).thenReturn(rows);

        var db = new EmailDb();
        db.setId(1L);
        db.setClientId(10L);
        db.setSubject("Subject");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.getLatestAllByClientId(Set.of(10L)))
                .assertNext(email -> {
                    assertEquals(1L, email.getId());
                    assertEquals("Subject", email.getSubject());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("emails"));
        assertTrue(sqlCaptor.getValue().contains("ROW_NUMBER"));
        verify(spec).bind("ids", Set.of(10L));
    }
}
