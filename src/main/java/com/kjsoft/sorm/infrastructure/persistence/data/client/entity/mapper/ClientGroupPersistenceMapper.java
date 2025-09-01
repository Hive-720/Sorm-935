package com.kjsoft.sorm.infrastructure.persistence.data.client.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.ClientGroup;
import com.kjsoft.sorm.infrastructure.persistence.data.client.entity.ClientGroupDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ClientGroupPersistenceMapper {

    ClientGroup map(ClientGroupDb clientGroupDb);

    ClientGroupDb map(ClientGroup clientGroup);
}