package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.clientPersistenceMapper;
import static org.junit.jupiter.api.Assertions.*;

class ClientPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ClientPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = clientPersistenceMapper();
    }

    private ClientDb db() {
        ClientDb db = new ClientDb();
        db.setId(1L);
        db.setClientGroupId(2L);
        db.setOwnerUserId(3L);
        db.setFullName("Ivanov Ivan");
        db.setFirstName("Ivan");
        db.setLastName("Ivanov");
        db.setCompanyName("ACME");
        db.setEmail("ivan@example.com");
        db.setAddress("Lenina 1");
        db.setCity("Moscow");
        db.setState("MSK");
        db.setPostcode("101000");
        db.setCountry("RU");
        db.setMsisdn("79990001122");
        db.setPhonecc(7);
        db.setPhoneNumber("9990001122");
        db.setTaxId("7700000000");
        db.setTelegram("@ivan");
        db.setJabber("ivan@jabber");
        db.setCurrency("RUB");
        db.setDefaultGateway("paypal");
        db.setStatus("Active");
        db.setCredit(new BigDecimal("100.00"));
        db.setTaxExempt(false);
        db.setLanguage("ru");
        db.setDateCreated(DATE);
        db.setDeleted(true);
        return db;
    }

    @Test
    void shouldMapDbToModel() {
        var result = mapper.map(db());

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientGroupId());
        assertEquals(3L, result.getOwnerUserId());
        assertEquals("Ivanov Ivan", result.getFullName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("Lenina 1", result.getAddress());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("101000", result.getPostcode());
        assertEquals("RU", result.getCountry());
        assertEquals("79990001122", result.getMsisdn());
        assertEquals(7, result.getPhonecc());
        assertEquals("9990001122", result.getPhoneNumber());
        assertEquals("7700000000", result.getTaxId());
        assertEquals("@ivan", result.getTelegram());
        assertEquals("ivan@jabber", result.getJabber());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getDefaultGateway());
        assertEquals("Active", result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getCredit());
        assertEquals(false, result.getTaxExempt());
        assertEquals("ru", result.getLanguage());
        assertEquals(DATE, result.getDateCreated());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client();
        model.setId(1L);
        model.setClientGroupId(2L);
        model.setOwnerUserId(3L);
        model.setFullName("Ivanov Ivan");
        model.setFirstName("Ivan");
        model.setLastName("Ivanov");
        model.setCompanyName("ACME");
        model.setEmail("ivan@example.com");
        model.setAddress("Lenina 1");
        model.setCity("Moscow");
        model.setState("MSK");
        model.setPostcode("101000");
        model.setCountry("RU");
        model.setMsisdn("79990001122");
        model.setPhonecc(7);
        model.setPhoneNumber("9990001122");
        model.setTaxId("7700000000");
        model.setTelegram("@ivan");
        model.setJabber("ivan@jabber");
        model.setCurrency("RUB");
        model.setDefaultGateway("paypal");
        model.setStatus("Active");
        model.setCredit(new BigDecimal("100.00"));
        model.setTaxExempt(false);
        model.setLanguage("ru");
        model.setDateCreated(DATE);
        model.setDeleted(true);

        ClientDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientGroupId());
        assertEquals(3L, result.getOwnerUserId());
        assertEquals("Ivanov Ivan", result.getFullName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("Lenina 1", result.getAddress());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("101000", result.getPostcode());
        assertEquals("RU", result.getCountry());
        assertEquals("79990001122", result.getMsisdn());
        assertEquals(7, result.getPhonecc());
        assertEquals("9990001122", result.getPhoneNumber());
        assertEquals("7700000000", result.getTaxId());
        assertEquals("@ivan", result.getTelegram());
        assertEquals("ivan@jabber", result.getJabber());
        assertEquals("RUB", result.getCurrency());
        assertEquals("paypal", result.getDefaultGateway());
        assertEquals("Active", result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getCredit());
        assertEquals(false, result.getTaxExempt());
        assertEquals("ru", result.getLanguage());
        assertEquals(DATE, result.getDateCreated());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ClientDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client) null));
    }
}
