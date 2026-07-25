package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.email.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.email.EmailPersistenceFilter;
import com.grunvald.sorm.service.whmcsdata.filter.EmailFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailFilterPersistenceMapper {

    EmailPersistenceFilter map(EmailFilter filter);
}