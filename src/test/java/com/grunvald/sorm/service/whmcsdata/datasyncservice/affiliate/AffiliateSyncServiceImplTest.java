package com.grunvald.sorm.service.whmcsdata.datasyncservice.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.AffiliateService;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.AffiliateProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.AffiliateMapper;
import com.grunvald.sorm.service.whmcsprovider.result.AffiliatesResult;
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
class AffiliateSyncServiceImplTest {

    @Mock
    private AffiliateService affiliateService;
    @Mock
    private WhmcsProvider provider;
    @Mock
    private AffiliateMapper mapper;

    private AffiliateSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AffiliateSyncServiceImpl(affiliateService, provider, mapper);
    }

    @Test
    void getActualData_mapsAffiliatesAndCarriesPagination() {
        var dto1 = new AffiliateProviderDto();
        dto1.setId(1L);
        var dto2 = new AffiliateProviderDto();
        dto2.setId(2L);
        var a1 = new Affiliate();
        a1.setId(1L);
        var a2 = new Affiliate();
        a2.setId(2L);

        when(provider.getAffiliates(0L, 10))
                .thenReturn(Mono.just(AffiliatesResult.builder().elements(List.of(dto1, dto2)).hasNextPage(true).nextOffset(10L).build()));
        when(mapper.map(dto1)).thenReturn(a1);
        when(mapper.map(dto2)).thenReturn(a2);

        StepVerifier.create(service.getActualData(0L, 10))
                .assertNext(page -> {
                    assertEquals(List.of(a1, a2), page.getElements());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_emptyResult_returnsEmptyPage() {
        when(provider.getAffiliates(0L, 10))
                .thenReturn(Mono.just(AffiliatesResult.builder().elements(List.of()).hasNextPage(false).nextOffset(0L).build()));

        StepVerifier.create(service.getActualData(0L, 10))
                .assertNext(page -> {
                    assertTrue(page.getElements().isEmpty());
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToAffiliateService() {
        var page = AffiliateSyncPage.builder().elements(List.of(new Affiliate())).build();
        when(affiliateService.saveOrUpdateAll(page.getElements())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();

        verify(affiliateService).saveOrUpdateAll(page.getElements());
    }
}
