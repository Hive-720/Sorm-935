package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientLoginPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientLoginFilterPersistenceMapper {

    ClientLoginPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter filter);
}