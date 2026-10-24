package com.grunvald.sorm.service.whmcsdata.datasyncservice.clientgroup;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ClientGroupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientGroupSyncServiceImplTest {

    @Mock
    private ClientService clientService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ClientGroupMapper mapper;

    private ClientGroupSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientGroupSyncServiceImpl(clientService, whmcs, mapper);
    }

    @Test
    void getActualData_mapsAllGroups() {
        var dto1 = new ClientGroupProviderDto();
        dto1.setId(1L);
        var dto2 = new ClientGroupProviderDto();
        dto2.setId(2L);
        var g1 = new ClientGroup("g1", null);
        g1.setId(1L);
        var g2 = new ClientGroup("g2", null);
        g2.setId(2L);

        when(whmcs.getClientGroups()).thenReturn(Flux.just(dto1, dto2));
        when(mapper.map(dto1)).thenReturn(g1);
        when(mapper.map(dto2)).thenReturn(g2);

        StepVerifier.create(service.getActualData())
                .expectNext(List.of(g1, g2))
                .verifyComplete();
    }

    @Test
    void getActualData_emptySource_returnsEmptyList() {
        when(whmcs.getClientGroups()).thenReturn(Flux.empty());

        StepVerifier.create(service.getActualData())
                .assertNext(list -> assertTrue(list.isEmpty()))
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToClientService() {
        var data = List.of(new ClientGroup("g", null));
        when(clientService.saveOrUpdateAllClientGroup(data)).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(data))
                .expectNext(true)
                .verifyComplete();

        verify(clientService).saveOrUpdateAllClientGroup(data);
        assertEquals("g", data.getFirst().getName());
    }
}
