package com.kjsoft.sorm.infrastructure.whmcsprovider.api.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jsoup.Jsoup;
import org.springframework.util.StringUtils;

import java.io.IOException;


public class WhmcsEmailMsgBodyFromHtmlDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext context) throws IOException {
        var html = p.getText();

        if (!StringUtils.hasText(html)) {
            return null;
        }

        return convertHtmlToText(html);
    }

    private String convertHtmlToText(String html) {
        var doc = Jsoup.parse(html);
        doc.select("br").append("\\n");
        doc.select("p").prepend("\\n\\n");
        doc.select("tr").after("\\n");
        doc.select("div").prepend("\\n");

        var text = doc.text().replaceAll("\\\\n", "\n");

        return text.replaceAll("\n{3,}", "\n\n").trim();
    }
}