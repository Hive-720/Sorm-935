package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientFilterPersistenceMapper {

    ClientPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ClientFilter filter);
}