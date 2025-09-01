package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.kjsoft.sorm.util.IpAddressUtil.*;
import static io.micrometer.common.util.StringUtils.isBlank;


@Component
public class IpAddressDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        var value = p.getText();
        if (isBlank(value)) {
            return null;
        }

        return isValidIp(value) ? value : null;
    }
}