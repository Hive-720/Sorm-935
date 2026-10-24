package com.grunvald.sorm.infrastructure.persistence.data.user;

import com.grunvald.sorm.infrastructure.persistence.data.user.entity.UserDb;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.relation.UserClientRelationDb;
import com.grunvald.sorm.infrastructure.persistence.datafilter.DatabaseClientAdapter;
import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.mapper.UserFilterPersistenceMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
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
import static com.grunvald.sorm.testutil.MapStructTestFactory.userClientRelationPersistenceMapper;
import static com.grunvald.sorm.testutil.MapStructTestFactory.userPersistenceMapper;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
class UserRepositoryImplTest {

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
    private UserFilterPersistenceMapper userFilterMapper;

    private UserRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl(
                databaseClient, databaseClientAdapter,
                userPersistenceMapper(), userClientRelationPersistenceMapper(), userFilterMapper);
        lenient().when(connection.createStatement(anyString())).thenReturn(statement);
        lenient().when(statement.bind(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.bindNull(anyInt(), any())).thenReturn(statement);
        lenient().when(statement.add()).thenReturn(statement);
        mockInConnection(databaseClient, connection);
    }

    private User user(Long id) {
        var user = new User();
        user.setId(id);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setFullName("First Last");
        user.setEmail("user@example.com");
        user.setDateCreated(DATE);
        return user;
    }

    private UserClientRelation relation(Long userId, Long clientId) {
        return new UserClientRelation(userId, clientId, DATE);
    }

    @Test
    void deleteAllRelations_bindsArraysAndReturnsTrueWhenRowsDeleted() {
        mockStatementExecution(statement, 2L);

        StepVerifier.create(repository.deleteAllRelations(List.of(relation(1L, 10L), relation(2L, 20L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM client_user_relations"));

        ArgumentCaptor<Object> userIdsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(0), userIdsCaptor.capture());
        assertArrayEquals(new Long[]{1L, 2L}, (Long[]) userIdsCaptor.getValue());

        ArgumentCaptor<Object> clientIdsCaptor = ArgumentCaptor.forClass(Object.class);
        verify(statement).bind(eq(1), clientIdsCaptor.capture());
        assertArrayEquals(new Long[]{10L, 20L}, (Long[]) clientIdsCaptor.getValue());
    }

    @Test
    void deleteAllRelations_returnsFalseWhenNoRowsDeleted() {
        mockStatementExecution(statement, 0L);

        StepVerifier.create(repository.deleteAllRelations(List.of(relation(1L, 10L))))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void addAllRelations_multipleRowsChainsStatementsAndReturnsTrue() {
        mockStatementExecution(statement, 1L, 1L);

        StepVerifier.create(repository.addAllRelations(List.of(relation(1L, 10L), relation(2L, 20L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("INSERT INTO client_user_relations"));
        verify(statement).add();
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, 10L);
        verify(statement, times(2)).bind(2, DATE);
    }

    @Test
    void findAllRelationsByUserIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<UserClientRelationDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(UserClientRelationDb.class)).thenReturn(rows);

        var db = new UserClientRelationDb();
        db.setUserId(1L);
        db.setClientId(10L);
        db.setCreatedAt(DATE);
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllRelationsByUserIdIn(Set.of(1L)))
                .assertNext(relation -> {
                    assertEquals(1L, relation.getUserId());
                    assertEquals(10L, relation.getClientId());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM client_user_relations"));
        verify(spec).bind("ids", Set.of(1L));
    }

    @Test
    void addAll_bindsValuesExecutesInsertAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.addAll(List.of(user(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        var sql = sqlCaptor.getValue();
        assertTrue(sql.contains("INSERT INTO users"));
        assertTrue(sql.contains("date_created"));
        assertTrue(sql.contains("$6"));
        verify(statement).bind(0, 1L);
        verify(statement).bind(1, "First");
        verify(statement).bind(4, "user@example.com");
        verify(statement).bind(5, DATE);
    }

    @Test
    void addAll_bindsNullForMissingFields() {
        mockStatementExecution(statement, 1L);
        var user = user(1L);
        user.setLastName(null);
        user.setFullName(null);

        StepVerifier.create(repository.addAll(List.of(user)))
                .expectNext(true)
                .verifyComplete();

        verify(statement).bindNull(2, String.class);
        verify(statement).bindNull(3, String.class);
    }

    @Test
    void updateAll_bindsValuesExecutesUpdateAndReturnsTrue() {
        mockStatementExecution(statement, 1L);

        StepVerifier.create(repository.updateAll(List.of(user(1L))))
                .expectNext(true)
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection).createStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE users"));
        verify(statement).bind(0, "First");
        verify(statement).bind(3, "user@example.com");
        verify(statement).bind(4, 1L);
    }

    @Test
    void findAllByIdIn_bindsIdsAndMapsRows() {
        var spec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(databaseClient.sql(anyString())).thenReturn(spec);
        when(spec.bind(eq("ids"), any())).thenReturn(spec);

        RowsFetchSpec<UserDb> rows = mock(RowsFetchSpec.class);
        when(spec.mapProperties(UserDb.class)).thenReturn(rows);

        var db = new UserDb();
        db.setId(1L);
        db.setFullName("First Last");
        db.setEmail("user@example.com");
        when(rows.all()).thenReturn(Flux.just(db));

        StepVerifier.create(repository.findAllByIdIn(Set.of(1L)))
                .assertNext(user -> {
                    assertEquals(1L, user.getId());
                    assertEquals("First Last", user.getFullName());
                })
                .verifyComplete();

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(databaseClient).sql(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("FROM users"));
        verify(spec).bind("ids", Set.of(1L));
    }
}
