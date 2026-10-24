package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientMapperTest {

    private final ClientMapper mapper = new ClientMapperImpl();

    private static ClientProviderDto fullDto() {
        var dto = new ClientProviderDto();
        dto.setId(1L);
        dto.setClientId(2L);
        dto.setOwnerUserId(3L);
        dto.setFirstName("Ivan");
        dto.setLastName("Petrov");
        dto.setFullName("Ivan Petrov");
        dto.setCompanyName("ACME");
        dto.setEmail("ivan@example.com");
        dto.setAddress1("addr1");
        dto.setAddress2("addr2");
        dto.setCity("Moscow");
        dto.setState("MSK");
        dto.setPostcode("123456");
        dto.setCountry("Russia");
        dto.setPhoneNumber(1234567);
        dto.setTaxId("TAX");
        dto.setTaxExempt(true);
        dto.setPhonecc("7");
        dto.setDefaultGateway("gw");
        dto.setGroupId(5L);
        dto.setStatus("Active");
        dto.setCredit(new BigDecimal("10.5"));
        dto.setLanguage("ru");
        dto.setTelegram("telegramName");
        dto.setJabber("jabberName");
        dto.setCurrencyCode("USD");
        return dto;
    }

    @Test
    void map_fullClientProviderDto_mapsAllFields() {
        Client result = mapper.map(fullDto());

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getClientGroupId());
        assertEquals(3L, result.getOwnerUserId());
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
        // combinePhone: конкатенация БЕЗ разделителя: "7" + 1234567
        assertEquals("71234567", result.getMsisdn());
        assertEquals(7, result.getPhonecc());
        assertEquals("1234567", result.getPhoneNumber());
        assertEquals("TAX", result.getTaxId());
        assertEquals("telegramName", result.getTelegram());
        assertEquals("jabberName", result.getJabber());
        assertEquals("USD", result.getCurrency());
        assertEquals("gw", result.getDefaultGateway());
        assertEquals("Active", result.getStatus());
        assertEquals(new BigDecimal("10.5"), result.getCredit());
        assertEquals(Boolean.TRUE, result.getTaxExempt());
        assertEquals("ru", result.getLanguage());
        assertNull(result.getDateCreated());
        assertFalse(result.isDeleted());
    }

    @Test
    void combinePhone_phoneccNull_mapsMsisdnNull() {
        var dto = fullDto();
        dto.setPhonecc(null);

        Client result = mapper.map(dto);

        assertNull(result.getMsisdn());
        assertEquals("1234567", result.getPhoneNumber());
    }

    @Test
    void combinePhone_phoneNumberNull_mapsMsisdnNull() {
        var dto = fullDto();
        dto.setPhoneNumber(null);

        Client result = mapper.map(dto);

        assertNull(result.getMsisdn());
        assertEquals(7, result.getPhonecc());
        assertNull(result.getPhoneNumber());
    }

    @Test
    void combineAddress_address1Null_usesAddress2() {
        var dto = fullDto();
        dto.setAddress1(null);

        Client result = mapper.map(dto);

        assertEquals("addr2", result.getAddress());
    }

    @Test
    void combineAddress_address2Null_usesAddress1() {
        var dto = fullDto();
        dto.setAddress2(null);

        Client result = mapper.map(dto);

        assertEquals("addr1", result.getAddress());
    }

    @Test
    void combineAddress_bothNull_mapsNullAddress() {
        var dto = fullDto();
        dto.setAddress1(null);
        dto.setAddress2(null);

        Client result = mapper.map(dto);

        assertNull(result.getAddress());
    }

    @Test
    void map_fullClientShortProviderDto_mapsAllFields() {
        var dto = new ClientShortProviderDto();
        dto.setId(1L);
        dto.setGroupId(5L);
        dto.setFirstName("Ivan");
        dto.setLastName("Petrov");
        dto.setCompanyName("ACME");
        dto.setEmail("ivan@example.com");
        dto.setStatus("Active");
        dto.setDateCreated(OffsetDateTime.parse("2026-01-02T03:04:05Z"));

        Client result = mapper.map(dto);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getClientGroupId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Petrov", result.getLastName());
        assertEquals("ACME", result.getCompanyName());
        assertEquals("ivan@example.com", result.getEmail());
        assertEquals("Active", result.getStatus());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDateCreated());
        assertNull(result.getOwnerUserId());
        assertNull(result.getFullName());
        assertNull(result.getAddress());
        assertNull(result.getMsisdn());
        assertNull(result.getPhonecc());
        assertNull(result.getCurrency());
        assertFalse(result.isDeleted());
    }

    @Test
    void map_nullClientProviderDto_returnsNull() {
        assertNull(mapper.map((ClientProviderDto) null));
    }

    @Test
    void map_nullClientShortProviderDto_returnsNull() {
        assertNull(mapper.map((ClientShortProviderDto) null));
    }
}
