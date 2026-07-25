package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientShortProviderDto;
import com.grunvald.sorm.util.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(source = "groupId", target = "clientGroupId")
    @Mapping(source = "currencyCode", target = "currency")
    @Mapping(source = ".", target = "msisdn", qualifiedByName = "combinePhone")
    @Mapping(source = ".", target = "address", qualifiedByName = "combineAddress")
    Client map(ClientProviderDto dto);

    @Mapping(target = "ownerUserId", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "city", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "postcode", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "msisdn", ignore = true)
    @Mapping(target = "phonecc", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "taxId", ignore = true)
    @Mapping(target = "telegram", ignore = true)
    @Mapping(target = "jabber", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "defaultGateway", ignore = true)
    @Mapping(target = "credit", ignore = true)
    @Mapping(target = "taxExempt", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(source = "groupId", target = "clientGroupId")
    Client map(ClientShortProviderDto dto);


    @Named("combinePhone")
    default String combinePhone(ClientProviderDto dto) {
        if (dto.getPhonecc() == null || dto.getPhoneNumber() == null) {
            return null;
        }

        return dto.getPhonecc() + dto.getPhoneNumber();
    }

    @Named("combineAddress")
    default String combineAddress(ClientProviderDto dto) {
        return StringUtils.concatStrings(dto.getAddress1(), dto.getAddress2(), ", ");
    }
}