package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ActivityLogFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.ActivityLog;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.ActivityLogService;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
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

import java.time.OffsetDateTime;
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
class ActivityLogResolverTest {

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    @Mock
    private DataLoader<Long, User> userDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private ActivityLogResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ActivityLogResolver(activityLogService, filterMappers.activityLogMapper,
                MapStructTestFactory.activityLogResultMapper());
    }

    private ActivityLogFilterInput sampleFilter() {
        ActivityLogFilterInput nestedAnd = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null);
        ActivityLogFilterInput nestedOr = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null);
        ActivityLogFilterInput nestedNot = new ActivityLogFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null);
        return new ActivityLogFilterInput(
                SampleFilters.client(),
                SampleFilters.user(),
                SampleFilters.loginBase(),
                SampleFilters.ipAddress(),
                SampleFilters.dateTime(SampleFilters.D10),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void activityLog_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog();
        model.setId(42L);
        model.setClientId(100L);
        model.setUserId(200L);
        model.setDescription("login");
        when(activityLogService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ActivityLogSearchResult("cur-9", true, List.of(model))));

        StepVerifier.create(resolver.activityLog(sampleFilter(), 25, 50, "cur-9"))
                .assertNext(result -> {
                    assertEquals("cur-9", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    ActivityLog first = result.result().get(0);
                    assertEquals("42", first.id());
                    assertEquals(100L, first.clientId());
                    assertEquals(200L, first.userId());
                    assertEquals("login", first.description());
                })
                .verifyComplete();

        ArgumentCaptor<ActivityLogFilter> filterCaptor = ArgumentCaptor.forClass(ActivityLogFilter.class);
        verify(activityLogService).search(filterCaptor.capture(), eq("cur-9"), eq(25), eq(50));
        ActivityLogFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertNotNull(filter.getUser());
        assertEquals("eq@mail.com", filter.getUser().getEmail().getEmail().getEq());
        assertEquals("user.*", filter.getUsername().getLogin().getRegexp());
        assertEquals("10.0.0.1", filter.getIpAddress().getIp().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D10), filter.getDate().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void activityLog_passesNullArgumentsThrough() {
        when(activityLogService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ActivityLogSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.activityLog(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(activityLogService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void activityLog_propagatesServiceError() {
        when(activityLogService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(resolver.activityLog(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("boom", e.getMessage()))
                .verify();
    }

    @Test
    void activityLog_emptyServiceResultCompletesEmpty() {
        when(activityLogService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.activityLog(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForActivityLog_loadsClientByClientId() {
        Client client = new Client(7L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(100L)).thenReturn(CompletableFuture.completedFuture(client));

        ActivityLog activityLog = new ActivityLog("42", 100L, 200L, null, null, null, null);

        StepVerifier.create(resolver.getClientForActivityLog(activityLog, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(100L);
    }

    @Test
    void getClientForActivityLog_nullResultCompletesEmpty() {
        when(clientDataLoader.load(100L)).thenReturn(CompletableFuture.completedFuture(null));

        ActivityLog activityLog = new ActivityLog("42", 100L, 200L, null, null, null, null);

        StepVerifier.create(resolver.getClientForActivityLog(activityLog, clientDataLoader))
                .verifyComplete();
    }

    @Test
    void getUserForActivityLog_loadsUserByUserId() {
        User user = new User(9L, null, null, null, null);
        when(userDataLoader.load(200L)).thenReturn(CompletableFuture.completedFuture(user));

        ActivityLog activityLog = new ActivityLog("42", 100L, 200L, null, null, null, null);

        StepVerifier.create(resolver.getUserForActivityLog(activityLog, userDataLoader))
                .expectNext(user)
                .verifyComplete();

        verify(userDataLoader).load(200L);
    }

    @Test
    void getUserForActivityLog_nullResultCompletesEmpty() {
        when(userDataLoader.load(200L)).thenReturn(CompletableFuture.completedFuture(null));

        ActivityLog activityLog = new ActivityLog("42", 100L, 200L, null, null, null, null);

        StepVerifier.create(resolver.getUserForActivityLog(activityLog, userDataLoader))
                .verifyComplete();
    }
}
