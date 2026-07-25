package com.grunvald.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.grunvald.sorm.util.IpAddressUtil.*;


public class WhmcsSplitIpAddressByLineBreak extends JsonDeserializer<List<String>> {

    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException, JacksonException {
        var value = p.getText();

        if (!StringUtils.hasText(value)) {
            return null;
        }

        return Arrays.stream(value.split("\\R"))
                .map(String::trim)
                .filter(ip -> !ip.isEmpty() && isValidIp(ip))
                .toList();
    }
}