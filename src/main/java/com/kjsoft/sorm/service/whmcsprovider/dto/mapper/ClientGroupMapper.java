package com.kjsoft.sorm.service.whmcsprovider.dto.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.kjsoft.sorm.service.whmcsprovider.dto.ClientGroupProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ClientGroupMapper {

    @Mapping(source = "groupName", target = "name")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ClientGroup map(ClientGroupProviderDto dto);
}