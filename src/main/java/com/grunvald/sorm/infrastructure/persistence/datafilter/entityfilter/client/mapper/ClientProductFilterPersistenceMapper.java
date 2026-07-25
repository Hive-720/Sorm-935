package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientProductFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProductFilterPersistenceMapper {

    ClientProductFilter map(com.grunvald.sorm.service.whmcsdata.filter.ClientProductFilter filter);
}