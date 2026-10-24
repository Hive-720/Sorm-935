package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.email.model.Email;
import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailMapperTest {

    private final EmailMapper mapper = new EmailMapperImpl();

    @Test
    void map_fullDto_passesMessageBodyAndClientIdParams() {
        var dto = new EmailProviderDto();
        dto.setId(1L);
        dto.setClientId(999L); // перекрывается параметром clientId
        dto.setSubject("subject");
        dto.setMessageBody("dto body"); // перекрывается параметром messageBody
        dto.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        dto.setTo("to@example.com");
        dto.setCc(List.of("cc1@example.com", "cc2@example.com"));
        dto.setBcc(List.of("bcc@example.com"));
        dto.setCreatedAt(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        dto.setUpdatedAt(OffsetDateTime.parse("2026-03-02T03:04:05Z"));

        Email result = mapper.map(dto, "param body", 5L);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getClientId());
        assertEquals("subject", result.getSubject());
        assertEquals("param body", result.getMessageBody());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals("to@example.com", result.getTo());
        assertEquals(List.of("cc1@example.com", "cc2@example.com"), result.getCc());
        assertEquals(List.of("bcc@example.com"), result.getBcc());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getUpdatedAt());
    }

    @Test
    void map_allNull_returnsNull() {
        assertNull(mapper.map(null, null, null));
    }

    @Test
    void map_nullDto_passesMessageBodyAndClientIdParams() {
        Email result = mapper.map(null, "param body", 5L);

        assertNotNull(result);
        assertEquals("param body", result.getMessageBody());
        assertEquals(5L, result.getClientId());
        assertNull(result.getId());
        assertNull(result.getSubject());
        assertNull(result.getCc());
        assertNull(result.getBcc());
    }
}
