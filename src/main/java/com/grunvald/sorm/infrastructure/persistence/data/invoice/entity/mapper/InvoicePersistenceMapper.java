package com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.invoice.model.Invoice;
import com.grunvald.sorm.service.exception.InternalErrorException;
import com.grunvald.sorm.infrastructure.persistence.data.invoice.entity.InvoiceDb;
import io.r2dbc.postgresql.codec.Json;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class InvoicePersistenceMapper {

    @Autowired
    protected ObjectMapper objectMapper;


    @Mapping(target = "items", source = "items", qualifiedByName = "jsonToListInvoiceItem")
    public abstract Invoice map(InvoiceDb invoice);

    @Mapping(target = "items", source = "items", qualifiedByName = "listInvoiceItemToJson")
    public abstract InvoiceDb map(Invoice invoice);

    @Named("jsonToListInvoiceItem")
    protected List<Invoice.InvoiceItem> jsonToListInvoiceItem(Json json) {
        if (json == null) {
            return null;
        }
        try {
            var type = new TypeReference<List<Invoice.InvoiceItem>>() {
            };
            return objectMapper.readValue(json.asString(), type);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting JSON to InvoiceItem list", e);
        }
    }

    @Named("listInvoiceItemToJson")
    protected Json listInvoiceItemToJson(List<Invoice.InvoiceItem> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        try {
            String jsonString = objectMapper.writeValueAsString(items);
            return Json.of(jsonString);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting InvoiceItem list to JSON", e);
        }
    }
}