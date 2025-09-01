package com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.mapper;

import com.kjsoft.sorm.infrastructure.whmcsprovider.api.response.GetEmailsResponse;
import com.kjsoft.sorm.service.whmcsprovider.dto.EmailProviderDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EmailProviderMapper {

    EmailProviderDto map(GetEmailsResponse.Emails.Email email);
}