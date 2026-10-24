package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.result.UserResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.grunvald.sorm.testutil.MapStructTestFactory.userResultMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserResultMapperTest {

    private UserResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = userResultMapper();
    }

    @Test
    void shouldMapSearchResultToResult() {
        var first = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User();
        first.setId(1L);
        first.setFullName("Ivanov Ivan");
        first.setFirstName("Ivan");
        var second = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User();
        second.setId(2L);
        second.setFullName("Petrov Petr");
        second.setFirstName("Petr");

        UserResult result = mapper.map(new UserSearchResult("cursor-1", false, List.of(first, second)));

        assertEquals("cursor-1", result.cursor());
        assertEquals(false, result.hasNextPage());
        assertEquals(2, result.result().size());
        assertEquals(new PersonBase("Ivanov Ivan", "Ivan", null), result.result().get(0).name());
        assertNull(result.result().get(0).clients());
        assertEquals(new PersonBase("Petrov Petr", "Petr", null), result.result().get(1).name());
        assertNull(result.result().get(1).clients());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
