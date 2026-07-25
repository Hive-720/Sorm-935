package com.grunvald.sorm.service.whmcsprovider.dto.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.client.model.ClientProduct;
import com.grunvald.sorm.service.whmcsprovider.dto.ClientProductProviderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ClientProductMapper {

    @Mapping(source = "pid", target = "productId")
    @Mapping(source = "assignedIps", target = "assignedIpAddresses")
    @Mapping(source = "dedicatedIp", target = "dedicatedIpAddress")
    @Mapping(source = "configOptions", target = "configs")
    ClientProduct map(ClientProductProviderDto dto);
}