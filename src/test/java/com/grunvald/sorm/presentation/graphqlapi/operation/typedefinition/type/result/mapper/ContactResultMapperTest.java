package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.ContactResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.contact.dto.ContactSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.contactResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactResultMapperTest {

    private ContactResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = contactResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        first.setId(1L);
        first.setFullName("Petrov Petr");
        first.setFirstName("Petr");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        second.setId(2L);
        second.setFullName("Sidorov Sidor");
        second.setFirstName("Sidor");

        ContactResult result = mapper.map(new ContactSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(new PersonBase("Petrov Petr", "Petr", null), result.result().get(0).name());
        assertEquals(new PersonBase("Sidorov Sidor", "Sidor", null), result.result().get(1).name());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
