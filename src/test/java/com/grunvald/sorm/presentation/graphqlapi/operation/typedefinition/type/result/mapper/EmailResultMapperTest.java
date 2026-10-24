package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.EmailResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.email.dto.EmailSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.emailResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailResultMapperTest {

    private EmailResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = emailResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        first.setId(1L);
        first.setTo("a@example.com");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email();
        second.setId(2L);
        second.setTo("b@example.com");

        EmailResult result = mapper.map(new EmailSearchResult("cursor-1", true, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(true, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(new EmailBase("a@example.com"), result.result().get(0).to());
        assertEquals(new EmailBase("b@example.com"), result.result().get(1).to());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
