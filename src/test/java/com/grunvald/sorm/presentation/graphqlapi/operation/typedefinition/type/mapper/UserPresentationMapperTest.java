package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.userPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private UserPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = userPresentationMapper();
    }

    private com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User source() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User();
        source.setId(1L);
        source.setFullName("Sidorov Sidor");
        source.setFirstName("Sidor");
        source.setLastName("Sidorov");
        source.setEmail("sidor@example.com");
        source.setDateCreated(DATE);
        return source;
    }

    @Test
    void shouldMapAllFields() {
        User result = mapper.map(source());

        assertEquals(1L, result.id());
        assertEquals(new PersonBase("Sidorov Sidor", "Sidor", "Sidorov"), result.name());
        assertEquals(new EmailBase("sidor@example.com"), result.email());
        assertNull(result.clients());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.dateCreated());
    }

    @Test
    void shouldMapNameToNullWhenFirstNameAndLastNameAreNull() {
        var source = source();
        source.setFirstName(null);
        source.setLastName(null);

        User result = mapper.map(source);

        assertNull(result.name());
    }

    @Test
    void shouldIgnoreClients() {
        var source = source();
        source.setFullName("No Name");
        source.setFirstName("A");
        source.setLastName("B");

        User result = mapper.map(source);

        assertNull(result.clients());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
