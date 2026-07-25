package com.grunvald.sorm.infrastructure.persistence.data.contact.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.infrastructure.persistence.data.contact.entity.ContactDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactPersistenceMapper {

    Contact map(ContactDb contact);

    ContactDb map(Contact contact);
}