package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientFilterPersistenceMapper {

    ClientPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.ClientFilter filter);
}