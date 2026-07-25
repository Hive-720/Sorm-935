package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientPersistenceMapper {

    Client map(ClientDb clientDb);

    ClientDb map(Client client);
}