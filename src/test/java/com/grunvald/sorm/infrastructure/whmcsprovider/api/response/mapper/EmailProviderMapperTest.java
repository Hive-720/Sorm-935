package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetEmailsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmailProviderMapperTest {

    private final EmailProviderMapper mapper = new EmailProviderMapperImpl();

    @Test
    void map_fullEmail_mapsAllFields() {
        var source = new GetEmailsResponse.Emails.Email();
        source.setId(1L);
        source.setClientId(2L);
        source.setSubject("subject");
        source.setMessageBody("message body");
        source.setDate(OffsetDateTime.parse("2026-01-02T03:04:05Z"));
        source.setTo("to@example.com");
        source.setCc(List.of("cc1@example.com", "cc2@example.com"));
        source.setBcc(List.of("bcc@example.com"));
        source.setCreatedAt(OffsetDateTime.parse("2026-02-02T03:04:05Z"));
        source.setUpdatedAt(OffsetDateTime.parse("2026-03-02T03:04:05Z"));

        EmailProviderDto result = mapper.map(source);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getClientId());
        assertEquals("subject", result.getSubject());
        assertEquals("message body", result.getMessageBody());
        assertEquals(OffsetDateTime.parse("2026-01-02T03:04:05Z"), result.getDate());
        assertEquals("to@example.com", result.getTo());
        assertEquals(List.of("cc1@example.com", "cc2@example.com"), result.getCc());
        assertEquals(List.of("bcc@example.com"), result.getBcc());
        assertEquals(OffsetDateTime.parse("2026-02-02T03:04:05Z"), result.getCreatedAt());
        assertEquals(OffsetDateTime.parse("2026-03-02T03:04:05Z"), result.getUpdatedAt());
    }

    @Test
    void map_null_returnsNull() {
        assertNull(mapper.map(null));
    }
}
