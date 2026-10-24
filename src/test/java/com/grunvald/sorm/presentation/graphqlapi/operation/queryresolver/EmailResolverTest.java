package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.EmailFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.EmailBaseFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.condition.StringRegExpExactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Email;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.EmailService;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
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
class EmailResolverTest {

    @Mock
    private EmailService emailService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private EmailResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new EmailResolver(emailService, filterMappers.emailMapper,
                MapStructTestFactory.emailResultMapper());
    }

    private EmailFilterInput sampleFilter() {
        EmailBaseFilter to = new EmailBaseFilter(
                new StringRegExpExactFilter("to-re", "to@mail.com", null), null, null, null);
        EmailBaseFilter cc = new EmailBaseFilter(
                new StringRegExpExactFilter("cc-re", "cc@mail.com", null), null, null, null);
        EmailBaseFilter bcc = new EmailBaseFilter(
                new StringRegExpExactFilter("bcc-re", "bcc@mail.com", null), null, null, null);
        EmailFilterInput nestedAnd = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null, null);
        EmailFilterInput nestedOr = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null, null);
        EmailFilterInput nestedNot = new EmailFilterInput(null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null, null);
        return new EmailFilterInput(
                SampleFilters.client(),
                to, cc, bcc,
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void emails_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        model.setId(1L);
        model.setClientId(2L);
        model.setSubject("Hello");
        when(emailService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new EmailSearchResult("cur-5", true, List.of(model))));

        StepVerifier.create(resolver.emails(sampleFilter(), 10, 20, "cur-5"))
                .assertNext(result -> {
                    assertEquals("cur-5", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Email first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.clientId());
                    assertEquals("Hello", first.subject());
                })
                .verifyComplete();

        ArgumentCaptor<EmailFilter> filterCaptor = ArgumentCaptor.forClass(EmailFilter.class);
        verify(emailService).search(filterCaptor.capture(), eq("cur-5"), eq(10), eq(20));
        EmailFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals("to-re", filter.getTo().getEmail().getRegexp());
        assertEquals("to@mail.com", filter.getTo().getEmail().getEq());
        assertEquals("cc@mail.com", filter.getCc().getEmail().getEq());
        assertEquals("bcc@mail.com", filter.getBcc().getEmail().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void emails_passesNullArgumentsThrough() {
        when(emailService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new EmailSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.emails(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(emailService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void emails_propagatesServiceError() {
        when(emailService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("email boom")));

        StepVerifier.create(resolver.emails(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("email boom", e.getMessage()))
                .verify();
    }

    @Test
    void emails_emptyServiceResultCompletesEmpty() {
        when(emailService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.emails(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForEmail_loadsClientByClientId() {
        Client client = new Client(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(client));

        Email email = new Email(1L, 2L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForEmail(email, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(2L);
    }

    @Test
    void getClientForEmail_nullResultCompletesEmpty() {
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(null));

        Email email = new Email(1L, 2L, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForEmail(email, clientDataLoader))
                .verifyComplete();
    }
}
