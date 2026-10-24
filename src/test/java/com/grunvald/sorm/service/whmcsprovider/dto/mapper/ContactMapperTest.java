package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactMapperTest {

    private final ContactMapper mapper = new ContactMapperImpl();

    private static ContactProviderDto fullDto() {
        var dto = new ContactProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setFirstName("Ivan");
        dto.setLastName("Petrov");
        dto.setCompanyName("ACME");
        dto.setEmail("ivan@example.com");
        dto.setAddress1("addr1");
        dto.setAddress2("addr2");
        dto.setCity("Moscow");
        dto.setState("MSK");
        dto.setPostcode("123456");
        dto.setCountry("Russia");
        dto.setPhoneNumber("71234567890");
        dto.setTaxId("TAX");
        dto.setCreatedAt(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setUpdatedAt(OffsetDateTime.parse("2026-02-03T04:05:06Z"));
        return dto;
    }

    @Test
    void map_fullDto_mapsAllFields() {
        Contact result = mapper.map(fullDto());

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Ivan Petrov", result.getFullName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("addr1, addr2", result.getAddress());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("123456", result.getPostcode());
        assertEquals("Russia", result.getCountry());
        assertEquals("71234567890", result.getMsisdn());
        assertEquals("TAX", result.getTaxId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-02-03T04:05:06Z"), result.getUpdatedAt());
        assertFalse(result.isDeleted());
    }

    @Test
    void combineFullName_lastNameNull_usesFirstName() {
        var dto = fullDto();
        dto.setLastName(null);

        Contact result = mapper.map(dto);

        assertEquals("Ivan", result.getFullName());
    }

    @Test
    void combineFullName_bothNull_mapsNullFullName() {
        var dto = fullDto();
        dto.setFirstName(null);
        dto.setLastName(null);

        Contact result = mapper.map(dto);

        assertNull(result.getFullName());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
