package com.grunvald.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.grunvald.sorm.infrastructure.whmcsprovider.api.response.GetEmailsResponse;
import com.grunvald.sorm.service.whmcsprovider.dto.EmailProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailProviderMapper {

    EmailProviderDto map(GetEmailsResponse.Emails.Email email);
}