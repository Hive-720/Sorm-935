package com.kjsoft.sorm.infrastructure.persistence.data.product.entity.mapper.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjsoft.sorm.service.whmcsdata.dataservice.product.model.Price;
import com.kjsoft.sorm.service.exception.InternalErrorException;
import io.r2dbc.postgresql.codec.Json;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public abstract class PriceMapperConverter {

    @Autowired
    protected ObjectMapper objectMapper;


    @Named("jsonToPriceList")
    protected List<Price> jsonToPriceList(Json json) {
        if (json == null) {
            return null;
        }
        try {
            var type = new TypeReference<List<Price>>() {
            };
            return objectMapper.readValue(json.asString(), type);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting JSON to Price list", e);
        }
    }

    @Named("priceListToJson")
    protected Json priceListToJson(List<Price> prices) {
        if (prices == null || prices.isEmpty()) {
            return null;
        }
        try {
            var jsonString = objectMapper.writeValueAsString(prices);
            return Json.of(jsonString);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting Price list to JSON", e);
        }
    }
}