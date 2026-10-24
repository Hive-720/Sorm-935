package com.grunvald.sorm.service.whmcsdata.datasyncservice.contact;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.ContactService;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ContactMapper;
import com.grunvald.sorm.service.whmcsprovider.result.ContactsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactSyncServiceImplTest {

    @Mock
    private ContactService contactService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ContactMapper contactMapper;

    @Captor
    private ArgumentCaptor<Set<Long>> idSetCaptor;

    private ContactSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ContactSyncServiceImpl(contactService, whmcs, contactMapper);
    }

    private ContactProviderDto contactDto(long id) {
        var dto = new ContactProviderDto();
        dto.setId(id);
        return dto;
    }

    private Contact contact(long id) {
        var c = new Contact();
        c.setId(id);
        return c;
    }

    @Test
    void getActualData_mapsContactsAndCarriesPagination() {
        var dto1 = contactDto(1L);
        var dto2 = contactDto(2L);
        var c1 = contact(1L);
        var c2 = contact(2L);
        when(whmcs.getContacts(0L, 10))
                .thenReturn(Mono.just(ContactsResult.builder().elements(List.of(dto1, dto2)).hasNextPage(true).nextOffset(10L).build()));
        when(contactMapper.map(dto1)).thenReturn(c1);
        when(contactMapper.map(dto2)).thenReturn(c2);

        StepVerifier.create(service.getActualData(0L, 10))
                .assertNext(page -> {
                    assertEquals(List.of(c1, c2), page.getElements());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToContactService() {
        var page = ContactSyncPage.builder().elements(List.of(contact(1))).build();
        when(contactService.saveOrUpdateAll(page.getElements())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void delete_noActiveIds_skipsWhmcsAndReturnsTrue() {
        when(contactService.getAllActiveIds()).thenReturn(Mono.just(List.of()));

        StepVerifier.create(service.delete(0L, 10))
                .expectNext(true)
                .verifyComplete();

        verify(whmcs, never()).getContacts(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt());
        verify(contactService, never()).deleteAll(anySet());
    }

    @Test
    void delete_allContactsPresentInWhmcs_nothingDeleted() {
        when(contactService.getAllActiveIds()).thenReturn(Mono.just(List.of(1L, 2L)));
        when(whmcs.getContacts(0L, 10))
                .thenReturn(Mono.just(ContactsResult.builder()
                        .elements(List.of(contactDto(1L), contactDto(2L)))
                        .hasNextPage(false)
                        .nextOffset(null)
                        .build()));

        StepVerifier.create(service.delete(0L, 10))
                .expectNext(true)
                .verifyComplete();

        verify(contactService, never()).deleteAll(anySet());
    }

    @Test
    void delete_missingInWhmcs_removed() {
        when(contactService.getAllActiveIds()).thenReturn(Mono.just(List.of(1L, 2L, 3L)));
        when(whmcs.getContacts(0L, 10))
                .thenReturn(Mono.just(ContactsResult.builder()
                        .elements(List.of(contactDto(1L)))
                        .hasNextPage(false)
                        .nextOffset(null)
                        .build()));
        when(contactService.deleteAll(Set.of(2L, 3L))).thenReturn(Mono.just(true));

        StepVerifier.create(service.delete(0L, 10))
                .expectNext(true)
                .verifyComplete();

        verify(contactService).deleteAll(idSetCaptor.capture());
        assertEquals(Set.of(2L, 3L), idSetCaptor.getValue());
    }

    @Test
    void delete_expandsWhmcsPagination() {
        when(contactService.getAllActiveIds()).thenReturn(Mono.just(List.of(1L, 2L, 3L)));
        when(whmcs.getContacts(0L, 10))
                .thenReturn(Mono.just(ContactsResult.builder()
                        .elements(List.of(contactDto(1L)))
                        .hasNextPage(true)
                        .nextOffset(10L)
                        .build()));
        when(whmcs.getContacts(10L, 10))
                .thenReturn(Mono.just(ContactsResult.builder()
                        .elements(List.of(contactDto(2L)))
                        .hasNextPage(false)
                        .nextOffset(null)
                        .build()));
        when(contactService.deleteAll(Set.of(3L))).thenReturn(Mono.just(true));

        StepVerifier.create(service.delete(0L, 10))
                .expectNext(true)
                .verifyComplete();

        verify(contactService).deleteAll(idSetCaptor.capture());
        assertEquals(Set.of(3L), idSetCaptor.getValue());
    }
}
