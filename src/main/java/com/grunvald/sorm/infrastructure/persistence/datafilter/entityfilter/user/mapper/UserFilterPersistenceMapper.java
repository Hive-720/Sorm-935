package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserFilterPersistenceMapper {

    UserPersistenceFilter map(com.grunvald.sorm.service.whmcsdata.filter.UserFilter filter);
}