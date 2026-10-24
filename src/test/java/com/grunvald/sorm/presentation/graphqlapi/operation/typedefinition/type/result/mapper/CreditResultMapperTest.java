package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.CreditResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.credit.dto.CreditSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.creditResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreditResultMapperTest {

    private CreditResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = creditResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit();
        first.setId(1L);
        first.setDescription("first");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.credit.model.Credit();
        second.setId(2L);
        second.setDescription("second");

        CreditResult result = mapper.map(new CreditSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("1", result.result().get(0).id());
        assertEquals("first", result.result().get(0).description());
        assertEquals("2", result.result().get(1).id());
        assertEquals("second", result.result().get(1).description());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
