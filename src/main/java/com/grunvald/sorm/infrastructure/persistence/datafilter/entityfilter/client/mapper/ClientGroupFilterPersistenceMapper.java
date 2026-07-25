package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientGroupPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientGroupFilterPersistenceMapper {

    ClientGroupPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.ClientGroupFilter filter);
}