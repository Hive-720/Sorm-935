package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact;
import com.grunvald.sorm.service.whmcsprovider.dto.ContactProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.grunvald.sorm.util.StringUtils.concatStrings;


@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "phoneNumber", target = "msisdn")
    @Mapping(source = ".", target = "address", qualifiedByName = "combineAddress")
    @Mapping(source = ".", target = "fullName", qualifiedByName = "combineFullName")
    @Mapping(target = "deleted", ignore = true)
    Contact map(ContactProviderDto dto);

    @Named("combineAddress")
    default String combineAddress(ContactProviderDto dto) {
        return concatStrings(dto.getAddress1(), dto.getAddress2(), ", ");

    }

    @Named("combineFullName")
    default String combineFullName(ContactProviderDto dto) {
        return concatStrings(dto.getFirstName(), dto.getLastName(), " ");
    }
}