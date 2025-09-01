package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;
import com.kjsoft.sorm.service.whmcsprovider.dto.UserProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.kjsoft.sorm.util.StringUtils.concatStrings;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = ".", target = "fullName", qualifiedByName = "combineFullName")
    User map(UserProviderDto dto);

    @Named("combineFullName")
    default String combineFullName(UserProviderDto dto) {
        return concatStrings(dto.getFirstName(), dto.getLastName(), " ");
    }
}