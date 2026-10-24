package com.grunvald.sorm.service.whmcsdata.datasyncservice.client;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.dto.ClientIdsResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ClientMapper;
import com.grunvald.sorm.service.whmcsprovider.result.ClientDetailsResult;
import com.grunvald.sorm.service.whmcsprovider.result.ClientsResult;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientSyncServiceImplTest {

    private static final OffsetDateTime LOGIN_DATE = OffsetDateTime.parse("2026-01-01T10:00:00+03:00");
    private static final OffsetDateTime NEWER_LOGIN_DATE = OffsetDateTime.parse("2026-02-01T10:00:00+03:00");

    @Mock
    private ClientService clientService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ClientMapper clientMapper;

    private ClientSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientSyncServiceImpl(clientService, whmcs, clientMapper);
    }

    private ClientProviderDto clientDto(long id) {
        var dto = new ClientProviderDto();
        dto.setId(id);
        return dto;
    }

    private ClientLogin login(long clientId, OffsetDateTime date) {
        return new ClientLogin(clientId, "192.0.2.1", date);
    }

    // ---------- getActual ----------

    @Test
    void getActual_mapsProviderElementsToPage() {
        var shortDto = new ClientShortProviderDto();
        var client = new Client();
        client.setId(1L);
        when(whmcs.getClients(0, 100)).thenReturn(Mono.just(ClientsResult.builder()
                .elements(List.of(shortDto))
                .hasNextPage(true)
                .nextOffset(100)
                .build()));
        when(clientMapper.map(shortDto)).thenReturn(client);

        StepVerifier.create(service.getActual(0, 100))
                .assertNext(page -> {
                    assertEquals(List.of(client), page.getElements());
                    assertTrue(page.isHasNextPage());
                    assertEquals(100L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void getActual_emptyElements_producesEmptyPage() {
        when(whmcs.getClients(0, 100)).thenReturn(Mono.just(ClientsResult.builder()
                .elements(List.of())
                .hasNextPage(false)
                .nextOffset(0)
                .build()));

        StepVerifier.create(service.getActual(0, 100))
                .assertNext(page -> {
                    assertTrue(page.getElements().isEmpty());
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_savesAllElements() {
        var client = new Client();
        client.setId(1L);
        var page = ClientSyncPage.builder().elements(List.of(client)).build();
        when(clientService.saveAllClient(List.of(client))).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();
    }

    // ---------- getActualClientDetails ----------

    @Test
    void getActualClientDetails_noClients_returnsEmptyPage() {
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of())));

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .expectNext(ClientDetailsSyncPage.EMPTY)
                .verifyComplete();

        verify(clientService, org.mockito.Mockito.never()).getLatestLastLoginAllByClientId(any());
    }

    @Test
    void getActualClientDetails_deletedClient_addedToRemoved() {
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of()));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .deleted(true)
                .build()));

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertEquals(Set.of(5L), page.getRemovedClients());
                    assertTrue(page.getActual().isEmpty());
                    assertTrue(page.getLastLogins().isEmpty());
                    assertFalse(page.isHasNextPage());
                    assertEquals(5L, page.getCursor());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_activeClientWithoutLogin_mappedToActual() {
        var dto = clientDto(5L);
        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of()));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertEquals(List.of(client), page.getActual());
                    assertTrue(page.getRemovedClients().isEmpty());
                    assertTrue(page.getLastLogins().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_loginCollectedWhenNoStoredLogin() {
        var dto = clientDto(5L);
        var lastLoginDto = new ClientProviderDto.LastLogin();
        lastLoginDto.setIpAddress("192.0.2.1");
        lastLoginDto.setDate(LOGIN_DATE);
        dto.setLastLogin(lastLoginDto);

        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of()));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertEquals(1, page.getLastLogins().size());
                    var login = page.getLastLogins().getFirst();
                    assertEquals(5L, login.getClientId());
                    assertEquals("192.0.2.1", login.getIpAddress());
                    assertEquals(LOGIN_DATE, login.getDate());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_loginSuppressedWhenStoredIsNewer() {
        var dto = clientDto(5L);
        var lastLoginDto = new ClientProviderDto.LastLogin();
        lastLoginDto.setIpAddress("192.0.2.1");
        lastLoginDto.setDate(LOGIN_DATE);
        dto.setLastLogin(lastLoginDto);

        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of(login(5L, NEWER_LOGIN_DATE))));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertTrue(page.getLastLogins().isEmpty());
                    assertEquals(List.of(client), page.getActual());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_loginAcceptedWhenStoredIsOlder() {
        var dto = clientDto(5L);
        var lastLoginDto = new ClientProviderDto.LastLogin();
        lastLoginDto.setIpAddress("192.0.2.2");
        lastLoginDto.setDate(NEWER_LOGIN_DATE);
        dto.setLastLogin(lastLoginDto);

        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of(login(5L, LOGIN_DATE))));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertEquals(1, page.getLastLogins().size());
                    assertEquals(NEWER_LOGIN_DATE, page.getLastLogins().getFirst().getDate());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_loginWithoutDateOrIp_isIgnored() {
        var dto = clientDto(5L);
        var lastLoginDto = new ClientProviderDto.LastLogin();
        lastLoginDto.setIpAddress(null);
        lastLoginDto.setDate(null);
        dto.setLastLogin(lastLoginDto);

        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of()));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertTrue(page.getLastLogins().isEmpty());
                    assertEquals(List.of(client), page.getActual());
                })
                .verifyComplete();
    }

    @Test
    void getActualClientDetails_lastLoginMissingFromDto_isIgnored() {
        var dto = clientDto(5L);
        dto.setLastLogin(null);

        var client = new Client();
        client.setId(5L);
        when(clientService.getAllClient(0L, 10)).thenReturn(Mono.just(
                new ClientIdsResult(0L, false, List.of(5L))));
        when(clientService.getLatestLastLoginAllByClientId(Set.of(5L)))
                .thenReturn(Mono.just(List.of()));
        when(whmcs.getClientDetails(5L)).thenReturn(Mono.just(ClientDetailsResult.builder()
                .element(dto)
                .deleted(false)
                .build()));
        when(clientMapper.map(dto)).thenReturn(client);

        StepVerifier.create(service.getActualClientDetails(0L, 10, 2))
                .assertNext(page -> {
                    assertTrue(page.getLastLogins().isEmpty());
                    assertEquals(List.of(client), page.getActual());
                })
                .verifyComplete();
    }

    // ---------- applyActualData(ClientDetailsSyncPage) ----------

    @Test
    void applyActualClientDetails_updatesSavesLoginsAndRemoves() {
        var client = new Client();
        client.setId(5L);
        var login = login(5L, LOGIN_DATE);
        var page = ClientDetailsSyncPage.builder()
                .actual(List.of(client))
                .lastLogins(List.of(login))
                .removedClients(Set.of(9L))
                .build();
        when(clientService.updateAllClient(List.of(client))).thenReturn(Mono.just(true));
        when(clientService.saveAllClientLogin(List.of(login))).thenReturn(Mono.just(true));
        when(clientService.removeAllClient(Set.of(9L))).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(clientService).updateAllClient(List.of(client));
        verify(clientService).saveAllClientLogin(List.of(login));
        verify(clientService).removeAllClient(Set.of(9L));
    }
}
