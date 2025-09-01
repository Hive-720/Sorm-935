package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.email.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.email.EmailPersistenceFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.EmailFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailFilterPersistenceMapper {

    EmailPersistenceFilter map(EmailFilter filter);
}