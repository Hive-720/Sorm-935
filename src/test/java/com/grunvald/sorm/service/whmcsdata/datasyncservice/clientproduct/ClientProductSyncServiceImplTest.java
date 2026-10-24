package com.grunvald.sorm.service.whmcsdata.datasyncservice.clientproduct;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.ClientService;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ClientProductMapper;
import com.grunvald.sorm.service.whmcsprovider.result.ClientProductsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientProductSyncServiceImplTest {

    @Mock
    private ClientService clientService;
    @Mock
    private WhmcsProvider whmcs;
    @Mock
    private ClientProductMapper mapper;

    private ClientProductSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientProductSyncServiceImpl(clientService, whmcs, mapper);
    }

    @Test
    void getActualData_mapsProductsAndCarriesPagination() {
        var dto1 = new ClientProductProviderDto();
        dto1.setId(1L);
        var dto2 = new ClientProductProviderDto();
        dto2.setId(2L);
        var p1 = new ClientProduct();
        p1.setId(1L);
        var p2 = new ClientProduct();
        p2.setId(2L);

        when(whmcs.getClientProducts(0L, 10))
                .thenReturn(Mono.just(ClientProductsResult.builder()
                        .elements(List.of(dto1, dto2))
                        .hasNextPage(true)
                        .nextOffset(10L)
                        .build()));
        when(mapper.map(dto1)).thenReturn(p1);
        when(mapper.map(dto2)).thenReturn(p2);

        StepVerifier.create(service.getActualData(0L, 10))
                .assertNext(page -> {
                    assertEquals(List.of(p1, p2), page.getElements());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_emptyResult_returnsEmptyPage() {
        when(whmcs.getClientProducts(0L, 10))
                .thenReturn(Mono.just(ClientProductsResult.builder()
                        .elements(List.of())
                        .hasNextPage(false)
                        .nextOffset(null)
                        .build()));

        StepVerifier.create(service.getActualData(0L, 10))
                .assertNext(page -> {
                    assertTrue(page.getElements().isEmpty());
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToClientService() {
        var page = ClientProductSyncPage.builder().elements(List.of(new ClientProduct())).build();
        when(clientService.saveOrUpdateAllClientProducts(page.getElements())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(clientService).saveOrUpdateAllClientProducts(page.getElements());
    }
}
