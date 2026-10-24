package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.AddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.InnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.MsisdnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.OrganizationInfoBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.contactPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ContactPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = contactPresentationMapper();
    }

    private com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact source() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        source.setId(1L);
        source.setClientId(2L);
        source.setFullName("Petrov Petr");
        source.setFirstName("Petr");
        source.setLastName("Petrov");
        source.setCompanyName("ACME");
        source.setEmail("petr@example.com");
        source.setAddress("Lenina 2");
        source.setCity("Moscow");
        source.setState("MSK");
        source.setPostcode("101000");
        source.setCountry("RU");
        source.setMsisdn("79990002233");
        source.setTaxId("7700000001");
        source.setCreatedAt(DATE);
        source.setUpdatedAt(DATE);
        source.setDeleted(true);
        return source;
    }

    @Test
    void shouldMapAllFields() {
        Contact result = mapper.map(source());

        assertEquals(1L, result.id());
        assertEquals(2L, result.clientId());
        assertEquals(new PersonBase("Petrov Petr", "Petr", "Petrov"), result.name());
        assertEquals(new OrganizationInfoBase("ACME"), result.organizationInfo());
        assertEquals(new EmailBase("petr@example.com"), result.email());
        assertEquals(new AddressBase("Lenina 2", "101000", "RU", "MSK", "Moscow"), result.address());
        assertEquals(new InnBase("7700000001"), result.inn());
        assertEquals(new MsisdnBase("79990002233"), result.msisdn());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.createdAt());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.updatedAt());
        assertEquals(true, result.deleted());
    }

    @Test
    void shouldMapNameToNullWhenFirstNameAndLastNameAreNull() {
        var source = source();
        source.setFirstName(null);
        source.setLastName(null);

        Contact result = mapper.map(source);

        assertNull(result.name());
    }

    @Test
    void shouldMapAddressToNullWhenAllAddressFieldsAreNull() {
        var source = source();
        source.setAddress(null);
        source.setPostcode(null);
        source.setCountry(null);
        source.setState(null);
        source.setCity(null);

        Contact result = mapper.map(source);

        assertNull(result.address());
    }

    @Test
    void shouldMapNameWhenOnlyFirstNamePresent() {
        var source = source();
        source.setLastName(null);

        Contact result = mapper.map(source);

        assertEquals(new PersonBase("Petrov Petr", "Petr", null), result.name());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
