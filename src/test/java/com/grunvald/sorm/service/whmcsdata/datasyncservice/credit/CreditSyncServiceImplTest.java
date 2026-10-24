package com.grunvald.sorm.service.whmcsdata.datasyncservice.credit;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientIdsResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.CreditService;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.CreditProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.CreditMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditSyncServiceImplTest {

    private static final OffsetDateTime HISTORY_DEPTH = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Mock
    private CreditService creditService;
    @Mock
    private ClientService clientService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private CreditMapper creditMapper;

    private CreditSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CreditSyncServiceImpl(creditService, clientService, whmcs, creditMapper);
    }

    private CreditProviderDto creditDto(long id) {
        var dto = new CreditProviderDto();
        dto.setId(id);
        return dto;
    }

    private Credit credit(long id, long clientId) {
        var c = new Credit();
        c.setId(id);
        c.setClientId(clientId);
        return c;
    }

    @Test
    void getActualData_noClients_returnsEmptyPage() {
        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(new ClientIdsResult(5L, false, List.of())));

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH))
                .expectNext(CreditSyncPage.EMPTY)
                .verifyComplete();

        verify(whmcs, never()).getCredits(org.mockito.ArgumentMatchers.anyLong(), any());
    }

    @Test
    void getActualData_mapsCreditsForEachClient() {
        var ids = new ClientIdsResult(5L, false, List.of(1L, 2L));
        var dto1 = creditDto(101L);
        var dto2 = creditDto(102L);
        var credit1 = credit(101L, 1L);
        var credit2 = credit(102L, 2L);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(creditService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        when(whmcs.getCredits(1L, HISTORY_DEPTH)).thenReturn(Flux.just(dto1));
        when(whmcs.getCredits(2L, HISTORY_DEPTH)).thenReturn(Flux.just(dto2));
        when(creditMapper.map(dto1, 1L)).thenReturn(credit1);
        when(creditMapper.map(dto2, 2L)).thenReturn(credit2);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertEquals(List.of(credit1, credit2), page.getCredits());
                    assertFalse(page.isHasNextPage());
                    assertEquals(2L, page.getCursor());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_latestCreditAfterHistoryDepth_usedAsMinDate() {
        var ids = new ClientIdsResult(5L, false, List.of(1L));
        var latestDate = OffsetDateTime.parse("2026-03-01T00:00:00Z");
        var latest = credit(100L, 1L);
        latest.setDate(latestDate);
        var dto = creditDto(101L);
        var mapped = credit(101L, 1L);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(creditService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of(latest)));
        when(whmcs.getCredits(1L, latestDate)).thenReturn(Flux.just(dto));
        when(creditMapper.map(dto, 1L)).thenReturn(mapped);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH))
                .assertNext(page -> assertEquals(List.of(mapped), page.getCredits()))
                .verifyComplete();
    }

    @Test
    void getActualData_applyDataLimit_recomputesCursor() {
        var ids = new ClientIdsResult(5L, false, List.of(1L, 2L));
        var dtoA = creditDto(101L);
        var dtoB = creditDto(102L);
        var creditA = credit(101L, 1L);
        var creditB = credit(102L, 1L);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(creditService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        // take(limit + 1) останавливает concatMap на кредитах первого клиента, второй не опрашивается
        when(whmcs.getCredits(1L, HISTORY_DEPTH)).thenReturn(Flux.just(dtoA, dtoB));
        when(creditMapper.map(dtoA, 1L)).thenReturn(creditA);
        when(creditMapper.map(dtoB, 1L)).thenReturn(creditB);

        StepVerifier.create(service.getActualData(5L, 10, 1, HISTORY_DEPTH))
                .assertNext(page -> {
                    assertTrue(page.isHasNextPage());
                    assertEquals(1L, page.getCursor(), "курсор — последний клиент, чьи кредиты вошли в лимит");
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToCreditService() {
        var page = CreditSyncPage.builder().credits(List.of(credit(1, 1))).build();
        when(creditService.saveAll(page.getCredits())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(creditService).saveAll(page.getCredits());
    }
}
