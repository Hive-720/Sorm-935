package com.grunvald.sorm.service.whmcsdata.datasyncservice.email;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientIdsResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.EmailService;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.EmailMapper;
import com.grunvald.sorm.service.whmcsprovider.result.EmailsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailSyncServiceImplTest {

    private static final OffsetDateTime HISTORY_DEPTH = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ClientService clientService;
    @Mock
    private EmailService emailService;
    @Mock
    private EmailMapper emailMapper;

    private EmailSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailSyncServiceImpl(whmcs, clientService, emailService, emailMapper);
    }

    private EmailProviderDto emailDto(long id, String body) {
        var dto = new EmailProviderDto();
        dto.setId(id);
        dto.setMessageBody(body);
        return dto;
    }

    private Email email(long id, long clientId, OffsetDateTime date) {
        var e = new Email();
        e.setId(id);
        e.setClientId(clientId);
        e.setDate(date);
        return e;
    }

    @Test
    void getActualData_noClients_returnsEmptyPage() {
        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(new ClientIdsResult(5L, false, List.of())));

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, null))
                .expectNext(EmailSyncPage.EMPTY)
                .verifyComplete();

        verify(whmcs, never()).getEmails(anyLong(), anyLong(), org.mockito.ArgumentMatchers.anyInt(), any());
    }

    @Test
    void getActualData_mapsEmailsForEachClient() {
        var ids = new ClientIdsResult(5L, false, List.of(1L, 2L));
        var dto1 = emailDto(101L, "body1");
        var dto2 = emailDto(102L, "body2");
        var email1 = email(101L, 1L, OffsetDateTime.parse("2026-01-10T00:00:00Z"));
        var email2 = email(102L, 2L, OffsetDateTime.parse("2026-01-11T00:00:00Z"));

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        when(whmcs.getEmails(1L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto1)).hasNextPage(false).nextOffset(null).build()));
        when(whmcs.getEmails(2L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto2)).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(dto1, "body1", 1L)).thenReturn(email1);
        when(emailMapper.map(dto2, "body2", 2L)).thenReturn(email2);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, null))
                .assertNext(page -> {
                    assertTrue(page.getEmails().containsAll(List.of(email1, email2)));
                    assertFalse(page.isHasNextPage());
                    assertEquals(2L, page.getCursor(), "курсор — последний обработанный clientId");
                })
                .verifyComplete();
    }

    @Test
    void getActualData_expandsPaginationUntilLastPage() {
        var ids = new ClientIdsResult(5L, false, List.of(1L));
        var dto1 = emailDto(101L, "page1");
        var dto2 = emailDto(102L, "page2");
        var email1 = email(101L, 1L, null);
        var email2 = email(102L, 1L, null);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        when(whmcs.getEmails(1L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto1)).hasNextPage(true).nextOffset(10L).build()));
        when(whmcs.getEmails(1L, 10L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto2)).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(dto1, "page1", 1L)).thenReturn(email1);
        when(emailMapper.map(dto2, "page2", 1L)).thenReturn(email2);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, null))
                .assertNext(page -> assertEquals(2, page.getEmails().size()))
                .verifyComplete();
    }

    @Test
    void getActualData_applyDataLimit_recomputesCursor() {
        var ids = new ClientIdsResult(5L, false, List.of(1L, 2L));
        var dtoA = emailDto(101L, "a");
        var dtoB = emailDto(102L, "b");
        var emailA = email(101L, 1L, null);
        var emailB = email(102L, 1L, null);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        // один клиент, два письма: flatMapIterable сохраняет порядок
        when(whmcs.getEmails(1L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dtoA, dtoB)).hasNextPage(false).nextOffset(null).build()));
        when(whmcs.getEmails(2L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of()).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(dtoA, "a", 1L)).thenReturn(emailA);
        when(emailMapper.map(dtoB, "b", 1L)).thenReturn(emailB);

        StepVerifier.create(service.getActualData(5L, 10, 1, HISTORY_DEPTH, null))
                .assertNext(page -> {
                    assertTrue(page.isHasNextPage(), "собрано limit+1 писем — есть ещё данные");
                    assertEquals(1L, page.getCursor(), "курсор возвращается к последнему включённому clientId");
                })
                .verifyComplete();
    }

    @Test
    void getActualData_latestEmailAfterHistoryDepth_usedAsMinDate() {
        var ids = new ClientIdsResult(5L, false, List.of(1L));
        var latestDate = OffsetDateTime.parse("2026-03-01T00:00:00Z");
        var latest = email(100L, 1L, latestDate);
        var dto = emailDto(101L, "body");
        var mapped = email(101L, 1L, null);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of(latest)));
        when(whmcs.getEmails(1L, 0L, 10, latestDate))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto)).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(dto, "body", 1L)).thenReturn(mapped);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, null))
                .assertNext(page -> assertTrue(page.getEmails().contains(mapped)))
                .verifyComplete();
    }

    @Test
    void getActualData_maskPatterns_appliedToMessageBody() {
        var ids = new ClientIdsResult(5L, false, List.of(1L));
        var dto = emailDto(101L, "card 1234 end");
        var mapped = email(101L, 1L, null);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        when(whmcs.getEmails(1L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto)).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(eq(dto), eq("card **** end"), eq(1L))).thenReturn(mapped);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, List.of("\\d+")))
                .assertNext(page -> assertTrue(page.getEmails().contains(mapped)))
                .verifyComplete();
    }

    @Test
    void getActualData_emptyPatternsAndNullBody_passThroughUnchanged() {
        var ids = new ClientIdsResult(5L, false, List.of(1L));
        var dto = emailDto(101L, null);
        var mapped = email(101L, 1L, null);

        when(clientService.getAllClient(5L, 10)).thenReturn(Mono.just(ids));
        when(emailService.getLatestAllByClientId(any())).thenReturn(Mono.just(List.of()));
        when(whmcs.getEmails(1L, 0L, 10, HISTORY_DEPTH))
                .thenReturn(Mono.just(EmailsResult.builder().elements(List.of(dto)).hasNextPage(false).nextOffset(null).build()));
        when(emailMapper.map(dto, null, 1L)).thenReturn(mapped);

        StepVerifier.create(service.getActualData(5L, 10, 100, HISTORY_DEPTH, null))
                .assertNext(page -> assertTrue(page.getEmails().contains(mapped)))
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToEmailService() {
        var page = EmailSyncPage.builder().emails(List.of(email(1, 1, null))).build();
        when(emailService.saveAll(page.getEmails())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(emailService).saveAll(page.getEmails());
    }
}
