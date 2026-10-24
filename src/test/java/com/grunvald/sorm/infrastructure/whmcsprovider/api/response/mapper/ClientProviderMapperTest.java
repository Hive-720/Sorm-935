package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.config.Config;
import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientProviderMapperTest {

    private static final ObjectMapper OBJECT_MAPPER = new Config().objectMapper();

    private final ClientProviderMapper mapper = new ClientProviderMapperImpl();

    private static <T> T fromJson(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new AssertionError("Failed to parse fixture json", e);
        }
    }

    @Test
    void map_fullResponse_mapsAllFields() {
        var source = fromJson("""
                {
                  "userid": 1,
                  "client_id": 2,
                  "id": 3,
                  "owner_user_id": 4,
                  "firstname": "Ivan",
                  "lastname": "Petrov",
                  "fullname": "Ivan Petrov",
                  "companyname": "ACME",
                  "email": "ivan@example.com",
                  "address1": "addr1",
                  "address2": "addr2",
                  "city": "Moscow",
                  "fullstate": "FS",
                  "state": "MSK",
                  "postcode": "123456",
                  "countrycode": "RU",
                  "country": "Russia",
                  "phonenumber": "1234567",
                  "tax_id": "TAX",
                  "taxexempt": true,
                  "phonecc": "7",
                  "defaultgateway": "gw",
                  "groupid": 7,
                  "status": "Active",
                  "credit": 10.5,
                  "language": "ru",
                  "lastlogin": "Date: 01.02.2026 03:04<br />IP Address: 1.2.3.4",
                  "customfields5": "telegramName",
                  "customfields6": "jabberName"
                }
                """, GetClientsDetailsResponse.class);

        ClientProviderDto result = mapper.map(source);

        assertEquals(3L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals(4L, result.getOwnerUserId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("Ivan Petrov", result.getFullName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("addr1", result.getAddress1());
        assertEquals("addr2", result.getAddress2());
        assertEquals("Moscow", result.getCity());
        assertEquals("MSK", result.getState());
        assertEquals("123456", result.getPostcode());
        assertEquals("Russia", result.getCountry());
        assertEquals(1234567, result.getPhoneNumber());
        assertEquals("TAX", result.getTaxId());
        assertEquals(Boolean.TRUE, result.getTaxExempt());
        assertEquals("7", result.getPhonecc());
        assertEquals("gw", result.getDefaultGateway());
        assertEquals(7L, result.getGroupId());
        assertEquals("Active", result.getStatus());
        assertEquals(new BigDecimal("10.5"), result.getCredit());
        assertEquals("ru", result.getLanguage());
        assertNull(result.getCurrencyCode());

        assertEquals("1.2.3.4", result.getLastLogin().getIpAddress());
        assertEquals(OffsetDateTime.parse("2026-02-01T03:04:00Z"), result.getLastLogin().getDate());
        assertEquals("telegramName", result.getTelegram());
        assertEquals("jabberName", result.getJabber());
    }

    @Test
    void map_responseWithoutPhoneNumber_mapsNullPhoneNumber() {
        var source = fromJson("{\"id\": 1}", GetClientsDetailsResponse.class);

        ClientProviderDto result = mapper.map(source);

        assertNull(result.getPhoneNumber());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
