package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserFilterPersistenceMapper {

    UserPersistenceFilter map(com.kjsoft.sorm.service.whmcsdata.filter.UserFilter filter);
}