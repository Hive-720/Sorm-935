package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.Client;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.AddressBase;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface ClientPresentationMapper {

    @Mapping(target = "name", source = "client", qualifiedByName = "clientToPersonBase")
    @Mapping(target = "organizationInfo", source = "companyName", qualifiedByName = "stringToOrganisationInfoBase")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailBase")
    @Mapping(target = "address", source = "client", qualifiedByName = "clientToAddressBase")
    @Mapping(target = "inn", source = "taxId", qualifiedByName = "stringToInnBase")
    @Mapping(target = "msisdn", source = "msisdn", qualifiedByName = "stringToMsisdnBase")
    @Mapping(target = "mcc", source = "phonecc", qualifiedByName = "integerToMccBase")
    @Mapping(target = "dateCreated", source = "dateCreated", qualifiedByName = "offsetDateTimeToDateTimeBase")
    Client map(com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client client);

    @Named("clientToPersonBase")
    default PersonBase clientToPersonBase(com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client client) {
        var firstName = client.getFirstName();
        var lastName = client.getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

        return new PersonBase(client.getFullName(), firstName, lastName);
    }

    @Named("clientToAddressBase")
    default AddressBase clientToAddressBase(com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client client) {
        var address = client.getAddress();
        var zip = client.getPostcode();
        var country = client.getCountry();
        var region = client.getState();
        var city = client.getCity();

        if (address == null && zip == null && country == null && region == null && city == null) {
            return null;
        }

        return new AddressBase(address, zip, country, region, city);
    }
}