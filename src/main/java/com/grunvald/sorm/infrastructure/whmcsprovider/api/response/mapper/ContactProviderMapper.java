package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetContactsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactProviderMapper {

    ContactProviderDto map(GetContactsResponse.Contacts.Contact contact);
}