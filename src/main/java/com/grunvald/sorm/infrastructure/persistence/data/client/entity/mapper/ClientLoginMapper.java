package com.grunvald.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.client.entity.ClientLoginDb;
import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientLogin;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientLoginMapper {

    ClientLogin map(ClientLoginDb clientLoginDb);

    ClientLoginDb map(ClientLogin clientLogin);
}