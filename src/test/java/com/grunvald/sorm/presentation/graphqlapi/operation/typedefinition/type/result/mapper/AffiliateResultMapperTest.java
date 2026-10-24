package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.AffiliateResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.dto.AffiliateSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.affiliateResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AffiliateResultMapperTest {

    private AffiliateResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = affiliateResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate();
        first.setId(1L);
        first.setVisitors(10L);
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate();
        second.setId(2L);
        second.setVisitors(20L);

        AffiliateResult result = mapper.map(new AffiliateSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(1L, result.result().get(0).id());
        assertEquals(10, result.result().get(0).visitors());
        assertEquals(2L, result.result().get(1).id());
        assertEquals(20, result.result().get(1).visitors());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
