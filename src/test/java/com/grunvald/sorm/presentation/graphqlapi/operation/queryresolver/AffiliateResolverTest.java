package com.grunvald.sorm.presentation.graphqlapi.operation.queryresolver;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.AffiliateFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Affiliate;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.support.FilterMappers;
import com.grunvald.sorm.presentation.graphqlapi.support.SampleFilters;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.AffiliateService;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.AffiliateFilter;
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
class AffiliateResolverTest {

    @Mock
    private AffiliateService affiliateService;

    @Mock
    private DataLoader<Long, Client> clientDataLoader;

    private final FilterMappers filterMappers = new FilterMappers();

    private AffiliateResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AffiliateResolver(affiliateService,
                MapStructTestFactory.affiliateResultMapper(), filterMappers.affiliateMapper);
    }

    private AffiliateFilterInput sampleFilter() {
        AffiliateFilterInput nestedAnd = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D11),
                null, null, null, null, null);
        AffiliateFilterInput nestedOr = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D12),
                null, null, null, null, null);
        AffiliateFilterInput nestedNot = new AffiliateFilterInput(null, SampleFilters.dateTime(SampleFilters.D13),
                null, null, null, null, null);
        return new AffiliateFilterInput(
                SampleFilters.client(),
                SampleFilters.dateTime(SampleFilters.D01),
                SampleFilters.dateTime(SampleFilters.D02),
                SampleFilters.dateTime(SampleFilters.D03),
                List.of(nestedAnd), List.of(nestedOr), nestedNot);
    }

    @Test
    void affiliates_mapsFilterAndPassesPaginationArgumentsInOrder() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate();
        model.setId(1L);
        model.setClientId(2L);
        model.setVisitors(123L);
        model.setPayType("onetime");
        when(affiliateService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new AffiliateSearchResult("cur-2", true, List.of(model))));

        StepVerifier.create(resolver.affiliates(sampleFilter(), 5, 15, "cur-2"))
                .assertNext(result -> {
                    assertEquals("cur-2", result.cursor());
                    assertTrue(result.hasNextPage());
                    assertEquals(1, result.result().size());
                    Affiliate first = result.result().get(0);
                    assertEquals(1L, first.id());
                    assertEquals(2L, first.clientId());
                    assertEquals(123, first.visitors());
                    assertEquals("onetime", first.payType());
                })
                .verifyComplete();

        ArgumentCaptor<AffiliateFilter> filterCaptor = ArgumentCaptor.forClass(AffiliateFilter.class);
        verify(affiliateService).search(filterCaptor.capture(), eq("cur-2"), eq(5), eq(15));
        AffiliateFilter filter = filterCaptor.getValue();
        assertNotNull(filter.getClient());
        assertEquals("exact-eq", filter.getClient().getName().getFirstName().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D01), filter.getDate().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D02), filter.getCreatedAt().getUtc().getEq());
        assertEquals(OffsetDateTime.parse(SampleFilters.D03), filter.getUpdatedAt().getUtc().getEq());
        assertEquals(1, filter.getAnd().size());
        assertEquals(OffsetDateTime.parse(SampleFilters.D11), filter.getAnd().get(0).getDate().getUtc().getEq());
        assertEquals(1, filter.getOr().size());
        assertNotNull(filter.getNot());
    }

    @Test
    void affiliates_passesNullArgumentsThrough() {
        when(affiliateService.search(any(), any(), any(), any()))
                .thenReturn(Mono.just(new AffiliateSearchResult(null, false, List.of())));

        StepVerifier.create(resolver.affiliates(null, null, null, null))
                .assertNext(result -> {
                    assertNull(result.cursor());
                    assertFalse(result.hasNextPage());
                    assertEquals(0, result.result().size());
                })
                .verifyComplete();

        verify(affiliateService).search(isNull(), isNull(), isNull(), isNull());
    }

    @Test
    void affiliates_propagatesServiceError() {
        when(affiliateService.search(any(), any(), any(), any()))
                .thenReturn(Mono.error(new IllegalStateException("affiliate down")));

        StepVerifier.create(resolver.affiliates(sampleFilter(), 0, 10, null))
                .expectErrorSatisfies(e -> assertEquals("affiliate down", e.getMessage()))
                .verify();
    }

    @Test
    void affiliates_emptyServiceResultCompletesEmpty() {
        when(affiliateService.search(any(), any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(resolver.affiliates(sampleFilter(), 0, 10, null))
                .verifyComplete();
    }

    @Test
    void getClientForAffiliate_loadsClientByClientId() {
        Client client = new Client(2L, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null);
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(client));

        Affiliate affiliate = new Affiliate(1L, 2L, null, null, null, null, null, null,
                null, null, null);

        StepVerifier.create(resolver.getClientForAffiliate(affiliate, clientDataLoader))
                .expectNext(client)
                .verifyComplete();

        verify(clientDataLoader).load(2L);
    }

    @Test
    void getClientForAffiliate_nullResultCompletesEmpty() {
        when(clientDataLoader.load(2L)).thenReturn(CompletableFuture.completedFuture(null));

        Affiliate affiliate = new Affiliate(1L, 2L, null, null, null, null, null, null,
                null, null, null);

        StepVerifier.create(resolver.getClientForAffiliate(affiliate, clientDataLoader))
                .verifyComplete();
    }
}
