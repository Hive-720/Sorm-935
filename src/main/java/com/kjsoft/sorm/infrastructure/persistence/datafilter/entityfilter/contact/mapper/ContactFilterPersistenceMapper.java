package com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.contact.mapper;

import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.contact.ContactFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactFilterPersistenceMapper {

    ContactFilter map(com.kjsoft.sorm.service.whmcsdata.filter.ContactFilter filter);
}