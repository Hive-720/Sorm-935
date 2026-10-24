package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.TransactionResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.transaction.dto.TransactionSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.transactionResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionResultMapperTest {

    private TransactionResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = transactionResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction();
        first.setId(1L);
        first.setTransId("TX-1");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.transaction.model.Transaction();
        second.setId(2L);
        second.setTransId("TX-2");

        TransactionResult result = mapper.map(new TransactionSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals("TX-1", result.result().get(0).transId());
        assertEquals("TX-2", result.result().get(1).transId());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
