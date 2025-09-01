package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper;

import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.User;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.type.base.PersonBase;
import com.kjsoft.sorm.presentation.graphqlapi.util.MapperTypeConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = MapperTypeConverter.class)
public interface UserPresentationMapper {

    @Mapping(target = "name", source = "user", qualifiedByName = "userToPersonBase")
    @Mapping(target = "email", source = "email", qualifiedByName = "stringToEmailBase")
    @Mapping(target = "dateCreated", source = "dateCreated", qualifiedByName = "offsetDateTimeToDateTimeBase")
    @Mapping(target = "clients", ignore = true)
    User map(com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User user);

    @Named("userToPersonBase")
    default PersonBase clientToPersonBase(com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User user) {
        var firstName = user.getFirstName();
        var lastName = user.getLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

        return new PersonBase(user.getFullName(), firstName, lastName);
    }
}