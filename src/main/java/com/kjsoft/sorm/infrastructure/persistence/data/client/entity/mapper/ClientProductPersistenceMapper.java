package com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.kjsoft.sorm.service.exception.InternalErrorException;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientProductDb;
import io.r2dbc.postgresql.codec.Json;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class ClientProductPersistenceMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "configs", source = "configs", qualifiedByName = "jsonToListClientProductConfig")
    @Mapping(target = "assignedIpAddresses", source = "assignedIps")
    public abstract ClientProduct map(ClientProductDb clientProductDb);

    @Mapping(target = "configs", source = "configs", qualifiedByName = "listClientProductConfigToJson")
    @Mapping(target = "assignedIps", source = "assignedIpAddresses")
    public abstract ClientProductDb map(ClientProduct clientProduct);

    @Named("jsonToListClientProductConfig")
    protected List<ClientProduct.ConfigValue> jsonToListClientProductConfig(Json json) {
        if (json == null) {
            return null;
        }
        try {
            var type = new TypeReference<List<ClientProduct.ConfigValue>>() {};
            return objectMapper.readValue(json.asString(), type);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting JSON to ClientProductConfig list", e);
        }
    }

    @Named("listClientProductConfigToJson")
    protected Json listClientProductConfigToJson(List<ClientProduct.ConfigValue> configs) {
        if (configs == null) {
            return null;
        }
        try {
            String jsonString = objectMapper.writeValueAsString(configs);
            return Json.of(jsonString);
        } catch (JsonProcessingException e) {
            throw new InternalErrorException("Error converting ClientProductConfig list to JSON", e);
        }
    }
}