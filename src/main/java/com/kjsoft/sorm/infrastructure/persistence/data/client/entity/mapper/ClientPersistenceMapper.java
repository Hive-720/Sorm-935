package com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientPersistenceMapper {

    Client map(ClientDb clientDb);

    ClientDb map(Client client);
}