package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.client.ClientLoginPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientLoginFilterPersistenceMapper {

    ClientLoginPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ClientLoginFilter filter);
}