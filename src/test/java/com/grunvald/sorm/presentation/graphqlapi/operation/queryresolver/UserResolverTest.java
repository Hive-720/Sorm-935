package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.UserFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import com.grunvald.sorm.testutil.MapStructTestFactory;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserResolverTest {

    @Mock
    private UserService userService;

    @Mock
    private DataLoader<Long, List<Client>> clientsDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private UserResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new UserResolver(userService, filterMappers.userMapper,
                MapStructTestFactory.userResultMapper());
    }

    private UserFilterInput sampleFilter() {
        EmailBaseFilter andEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("and-re", "and@mail.com", null), null, null, null);
        EmailBaseFilter orEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("or-re", "or@mail.com", null), null, null, null);
        EmailBaseFilter notEmail = new EmailBaseFilter(
                new StringRegExpExactFilter("not-re", "not@mail.com", null), null, null, null);
        UserFilterInput nestedAnd = new UserFilterInput(null, andEmail, null, null, null);
        UserFilterInput nestedOr = new UserFilterInput(null, orEmail, null, null, null);
        UserFilterInput nestedNot = new UserFilterInput(null, notEmail, null, null, null);
        return new UserFilterInput(
                SampleFilters.person(),
                SampleFilters.emailBase(),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void users_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User();
        model.setId(1L);
        model.setFullName("Sidorov Sidor");
        model.setFirstName("Sidor");
        when(userService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new UserSearchResult("cur-15", true, List.of(model))));

        StepVerifier.create(resolver.users(sampleFilter(), 9, 99, "cur-15"))
                .assertNext(result -> {
                    assertEquals("cur-15", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    User first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals("Sidorov Sidor", first.name().fullName());
                    assertEquals("Sidor", first.name().firstName());
                })
                .verifyComplete();

        ArgumentCaptor<UserFilter> filterCaptor = ArgumentCaptor.forClass(UserFilter.class);
        verify(userService).search(filterCaptor.capture(), eq("cur-15"), eq(9), eq(99));
        UserFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getName());
        assertEquals("exact-eq", filter.getName().getFirstName().getEq());
        assertEquals(List.of("alpha", "beta"), filter.getName().getFullName().getAllOfTerms());
        assertEquals("last-eq", filter.getName().getLastName().getEq());
        assertNotNull(filter.getEmail());
        assertEquals("eq@mail.com", filter.getEmail().getEmail().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals("and@mail.com", filter.getAnd().get(0).getEmail().getEmail().getEq());
        assertEquals(1, filter.getOr().size());
        assertEquals("or@mail.com", filter.getOr().get(0).getEmail().getEmail().getEq());
        assertNotNull(filter.getNot());
        assertEquals("not@mail.com", filter.getNot().getEmail().getEmail().getEq());
    }

    @Test
    void users_passesNullArgumentsThrough() {
        when(userService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new UserSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.users(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(userService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void users_propagatesServiceError() {
        when(userService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("user boom")));

        StepVerifier.create(resolver.users(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("user boom", e.getMessage()))
                .verify();
    }

    @Test
    void users_emptyServiceResultCompletesEmpty() {
        when(userService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.users(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientsForUser_loadsClientsByUserId() {
        List<Client> clients = List.of(
                new Client(21L, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, null));
        when(clientsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(clients));

        User user = new User(1L, null, null, null, null);

        StepVerifier.create(resolver.getClientsForUser(user, clientsDataLoader))
                .expectNext(clients)
                .verifyComplete();

        verify(clientsDataLoader).load(1L);
    }

    @Test
    void getClientsForUser_nullResultCompletesEmpty() {
        when(clientsDataLoader.load(1L)).thenReturn(CompletableFuture.completedFuture(null));

        User user = new User(1L, null, null, null, null);

        StepVerifier.create(resolver.getClientsForUser(user, clientsDataLoader))
                .verifyComplete();
    }
}
