package com.grunvald.sorm.infrastructure.persistence.data.contact.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.ContactDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.grunvald.sorm.testutil.MapStructTestFactory.contactPersistenceMapper;
import static org.junit.jupiter.api.Assertions.*;

class ContactPersistenceMapperTest {

    private static final OffsetDateTime DATE = OffsetDateTime.of(2024, 1, 2, 3, 4, 5, 0, ZoneOffset.ofHours(3));

    private ContactPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = contactPersistenceMapper();
    }

    @Test
    void shouldMapDbToModel() {
        ContactDb db = new ContactDb();
        db.setId(1L);
        db.setClientId(2L);
        db.setFullName("Petrov Petr");
        db.setFirstName("Petr");
        db.setLastName("Petrov");
        db.setCompanyName("ACME");
        db.setEmail("petr@example.com");
        db.setAddress("Lenina 2");
        db.setCity("Moscow");
        db.setState("MSK");
        db.setPostcode("101000");
        db.setCountry("RU");
        db.setMsisdn("79990002233");
        db.setTaxId("7700000001");
        db.setCreatedAt(DATE);
        db.setUpdatedAt(DATE);
        db.setDeleted(true);

        var result = mapper.map(db);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Petrov Petr", result.getFullName());
        assertEquals("Petr", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("petr@example.com", result.getEmail());
        assertEquals("Lenina 2", result.getAddress());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("101000", result.getPostcode());
        assertEquals("RU", result.getCountry());
        assertEquals("79990002233", result.getMsisdn());
        assertEquals("7700000001", result.getTaxId());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapModelToDb() {
        var model = new com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact();
        model.setId(1L);
        model.setClientId(2L);
        model.setFullName("Petrov Petr");
        model.setFirstName("Petr");
        model.setLastName("Petrov");
        model.setCompanyName("ACME");
        model.setEmail("petr@example.com");
        model.setAddress("Lenina 2");
        model.setCity("Moscow");
        model.setState("MSK");
        model.setPostcode("101000");
        model.setCountry("RU");
        model.setMsisdn("79990002233");
        model.setTaxId("7700000001");
        model.setCreatedAt(DATE);
        model.setUpdatedAt(DATE);
        model.setDeleted(true);

        ContactDb result = mapper.map(model);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Petrov Petr", result.getFullName());
        assertEquals("Petr", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("petr@example.com", result.getEmail());
        assertEquals("Lenina 2", result.getAddress());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("101000", result.getPostcode());
        assertEquals("RU", result.getCountry());
        assertEquals("79990002233", result.getMsisdn());
        assertEquals("7700000001", result.getTaxId());
        assertEquals(DATE, result.getCreatedAt());
        assertEquals(DATE, result.getUpdatedAt());
        assertTrue(result.isDeleted());
    }

    @Test
    void shouldMapNullToNull() {
        assertNull(mapper.map((ContactDb) null));
        assertNull(mapper.map((com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact) null));
    }
}
