package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientGroupPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientGroupFilterPersistenceMapper {

    ClientGroupPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ClientGroupFilter filter);
}