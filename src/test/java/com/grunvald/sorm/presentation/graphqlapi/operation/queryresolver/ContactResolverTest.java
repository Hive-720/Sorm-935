package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ContactFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactService;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
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
class ContactResolverTest {

    @Mock
    private ContactService contactService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private ContactResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new ContactResolver(contactService, filterMappers.contactMapper,
                MapStructTestFactory.contactResultMapper());
    }

    private ContactFilterInput sampleFilter() {
        ContactFilterInput nestedAnd = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D11), null, null, null, null);
        ContactFilterInput nestedOr = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D12), null, null, null, null);
        ContactFilterInput nestedNot = new ContactFilterInput(null, null, null, null, null, null, null,
                SampleFilters.dateTime(SampleFilters.D13), null, null, null, null);
        return new ContactFilterInput(
                SampleFilters.client(),
                SampleFilters.person(),
                SampleFilters.organizationInfo(),
                SampleFilters.emailBase(),
                SampleFilters.address(),
                SampleFilters.inn(),
                SampleFilters.msisdn(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void contacts_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        model.setId(1L);
        model.setClientId(2L);
        model.setFullName("Petrov Petr");
        model.setFirstName("Petr");
        when(contactService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ContactSearchResult("cur-3", true, List.of(model))));

        StepVerifier.create(resolver.contacts(sampleFilter(), 30, 40, "cur-3"))
                .assertNext(result -> {
                    assertEquals("cur-3", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Contact first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.clientId());
                    assertEquals("Petrov Petr", first.name().fullName());
                    assertEquals("Petr", first.name().firstName());
                })
                .verifyComplete();

        ArgumentCaptor<ContactFilter> filterCaptor = ArgumentCaptor.forClass(ContactFilter.class);
        verify(contactService).search(filterCaptor.capture(), eq("cur-3"), eq(30), eq(40));
        ContactFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertNotNull(filter.getName());
        assertEquals("exact-eq", filter.getName().getFirstName().getEq());
        assertEquals("Grunvald", filter.getOrganizationInfo().getNameFull().getEq());
        assertEquals("eq@mail.com", filter.getEmail().getEmail().getEq());
        assertEquals("re.*", filter.getAddress().getAddress().getRegexp());
        assertEquals("inn-eq", filter.getInn().getInn().getEq());
        assertEquals("79990001122", filter.getMsisdn().getMsisdn().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getCreatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void contacts_passesNullArgumentsThrough() {
        when(contactService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new ContactSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.contacts(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(contactService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void contacts_propagatesServiceError() {
        when(contactService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new RuntimeException("contact boom")));

        StepVerifier.create(resolver.contacts(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("contact boom", e.getMessage()))
                .verify();
    }

    @Test
    void contacts_emptyServiceResultCompletesEmpty() {
        when(contactService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.contacts(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForContact_loadsClientByClientId() {
        Client client = new Client(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(client));

        Contact contact = new Contact(1L, 2L, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForContact(contact, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(2L);
    }

    @Test
    void getClientForContact_nullResultCompletesEmpty() {
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(null));

        Contact contact = new Contact(1L, 2L, null, null, null, null, null, null, null, null, null);

        StepVerifier.create(resolver.getClientForContact(contact, clientDataLoader))
                .verifyComplete();
    }
}
