package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientProductFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientProductFilterPersistenceMapper {

    ClientProductFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ClientProductFilter filter);
}