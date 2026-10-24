package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetContactsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContactProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final ContactProviderMapper mapper = new ContactProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullContact_mapsAllFields() {
        var source = fromJson("""
                {
                  "id": 1,
                  "userid": 2,
                  "firstname": "Ivan",
                  "lastname": "Petrov",
                  "companyname": "ACME",
                  "email": "ivan@example.com",
                  "address1": "addr1",
                  "address2": "addr2",
                  "city": "Moscow",
                  "state": "MSK",
                  "postcode": "123456",
                  "country": "Russia",
                  "phonenumber": "+7(123)456-78-90",
                  "tax_id": "TAX",
                  "created_at": "2026-01-02 03:04:05",
                  "updated_at": "2026-02-03 04:05:06"
                }
                """, GetContactsResponse.Contacts.Contact.class);

        ContactProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("addr1", result.getAddress1());
        assertEquals("addr2", result.getAddress2());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("123456", result.getPostcode());
        assertEquals("Russia", result.getCountry());
        assertEquals("71234567890", result.getPhoneNumber());
        assertEquals("TAX", result.getTaxId());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-02-03T04:05:06Z"), result.getUpdatedAt());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
