package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Contact;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.AddressBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.grunvald.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ContactPresentationMapper {

    @Mapping(target = "name", source = "contact", qualifiedByName = "contactToPersonBase")
    @Mapping(target = "organizationInfo", source = "companyName", qualifiedByName = "stringToOrganisationInfoBase")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailBase")
    @Mapping(target = "address", source = "contact", qualifiedByName = "contactToAddressBase")
    @Mapping(target = "inn", source = "taxId", qualifiedByName = "stringToInnBase")
    @Mapping(target = "msisdn", source = "msisdn", qualifiedByName = "stringToMsisdnBase")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Contact map(com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact contact);

    @Named("contactToPersonBase")
    default PersonBase contactToPersonBase(com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact contact) {
        var firstName = contact.getFirstName();
        var lastName = contact.getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

        return new PersonBase(contact.getFullName(), firstName, lastName);
    }

    @Named("contactToAddressBase")
    default AddressBase contactToAddressBase(com.grunvald.sorm.service.whmcsdata.dataservice.contact.model.Contact contact) {
        var address = contact.getAddress();
        var zip = contact.getPostcode();
        var country = contact.getCountry();
        var region = contact.getState();
        var city = contact.getCity();

        if (address == null && zip == null && country == null && region == null && city == null) {
            return null;
        }

        return new AddressBase(address, zip, country, region, city);
    }
}