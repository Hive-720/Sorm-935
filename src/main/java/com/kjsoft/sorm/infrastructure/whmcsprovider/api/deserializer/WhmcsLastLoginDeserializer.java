package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetClientsDetailsResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static com.kjsoft.sorm.util.IpAddressUtil.isValidIp;
import static graphql.com.google.common.base.Strings.emptyToNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.DOTALL;


@Slf4j
public class WhmcsLastLoginDeserializer extends JsonDeserializer<GetClientsDetailsResponse.LastLogin> {

    private static final Pattern PATTERN = Pattern.compile(
            "Date:\\s*(?<date>.+?)<.*?IP Address:\\s*(?<ip>.+?)(?:<|$)", CASE_INSENSITIVE | DOTALL
    );

    private static final String FORMAT = "dd.MM.yyyy HH:mm";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT);


    @Override
    public GetClientsDetailsResponse.LastLogin deserialize(JsonParser p, DeserializationContext context) throws IOException {
        var text = p.getText();

        if (text == null || text.isBlank()) {
            return null;
        }

        var matcher = PATTERN.matcher(text);

        if (matcher.find()) {
            var lastLogin = new GetClientsDetailsResponse.LastLogin();

            var dateStr = emptyToNull(matcher.group("date").trim());
            var ipStr = emptyToNull(matcher.group("ip").trim());

            if (isValidIp(ipStr)) {
                lastLogin.setIpAddress(ipStr);
            }

            try {
                if (dateStr != null) {
                    var localDateTime = LocalDateTime.parse(dateStr, DATE_FORMATTER);
                    lastLogin.setDate(localDateTime.atOffset(ZoneOffset.UTC));
                }
            } catch (Exception e) {
                log.warn("Failed to parse date string '{}'. Format expected: {}", dateStr, FORMAT);
            }

            if (lastLogin.getIpAddress() != null || lastLogin.getDate() != null) {
                return lastLogin;
            }
        }

        return null;
    }
}