package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetContactsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.ContactProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactProviderMapper {

    ContactProviderDto map(GetContactsResponse.Contacts.Contact contact);
}