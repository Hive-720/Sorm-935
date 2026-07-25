package com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.mapper;

import com.grunvald.sorm.infrastructure.persistence.datafilter.entityfilter.contact.ContactFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactFilterPersistenceMapper {

    ContactFilter map(com.grunvald.sorm.service.whmcsdata.filter.ContactFilter filter);
}