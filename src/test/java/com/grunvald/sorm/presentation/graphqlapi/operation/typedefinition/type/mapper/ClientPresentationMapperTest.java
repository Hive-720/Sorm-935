package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.AddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.EmailBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.InnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.MccBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.MsisdnBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.OrganizationInfoBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientPresentationMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientPresentationMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientPresentationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientPresentationMapper();
    }

    private com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client source() {
        var source = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        source.setId(1L);
        source.setClientGroupId(2L);
        source.setOwnerUserId(3L);
        source.setFullName("Ivanov Ivan");
        source.setFirstName("Ivan");
        source.setLastName("Ivanov");
        source.setCompanyName("ACME");
        source.setEmail("ivan@example.com");
        source.setAddress("Lenina 1");
        source.setCity("Moscow");
        source.setState("MSK");
        source.setPostcode("101000");
        source.setCountry("RU");
        source.setMsisdn("79990001122");
        source.setPhonecc(7);
        source.setPhoneNumber("9990001122");
        source.setTaxId("7700000000");
        source.setTelegram("@ivan");
        source.setJabber("ivan@jabber");
        source.setCurrency("RUB");
        source.setDefaultGateway("paypal");
        source.setStatus("Active");
        source.setCredit(new BigDecimal("100.00"));
        source.setTaxExempt(false);
        source.setLanguage("ru");
        source.setDateCreated(DATE);
        source.setDeleted(true);
        return source;
    }

    @Test
    void shouldMapAllFields() {
        Client result = mapper.map(source());

        assertEquals(1L, result.id());
        assertEquals(3L, result.ownerUserId());
        assertEquals(2L, result.clientGroupId());
        assertEquals(new PersonBase("Ivanov Ivan", "Ivan", "Ivanov"), result.name());
        assertEquals(new OrganizationInfoBase("ACME"), result.organizationInfo());
        assertEquals(new InnBase("7700000000"), result.inn());
        assertEquals(new EmailBase("ivan@example.com"), result.email());
        assertEquals(new AddressBase("Lenina 1", "101000", "RU", "MSK", "Moscow"), result.address());
        assertEquals(new MsisdnBase("79990001122"), result.msisdn());
        assertEquals(new MccBase(7), result.mcc());
        assertEquals("9990001122", result.phoneNumber());
        assertEquals("paypal", result.defaultGateway());
        assertEquals("@ivan", result.telegram());
        assertEquals("ivan@jabber", result.jabber());
        assertEquals("RUB", result.currency());
        assertEquals("Active", result.status());
        assertEquals(new BigDecimal("100.00"), result.credit());
        assertEquals(false, result.taxExempt());
        assertEquals("ru", result.language());
        assertEquals(new DateTimeBase("2024-01-02T00:04:05+00:00"), result.dateCreated());
        assertEquals(true, result.deleted());
    }

    @Test
    void shouldMapNameToNullWhenFirstNameAndLastNameAreNull() {
        var source = source();
        source.setFirstName(null);
        source.setLastName(null);

        Client result = mapper.map(source);

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

        Client result = mapper.map(source);

        assertNull(result.address());
    }

    @Test
    void shouldMapNameWhenOnlyFirstNamePresent() {
        var source = source();
        source.setLastName(null);

        Client result = mapper.map(source);

        assertEquals(new PersonBase("Ivanov Ivan", "Ivan", null), result.name());
    }

    @Test
    void shouldMapPartialAddress() {
        var source = source();
        source.setCity(null);
        source.setState(null);

        Client result = mapper.map(source);

        assertEquals(new AddressBase("Lenina 1", "101000", "RU", null, null), result.address());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map(null));
    }
}
