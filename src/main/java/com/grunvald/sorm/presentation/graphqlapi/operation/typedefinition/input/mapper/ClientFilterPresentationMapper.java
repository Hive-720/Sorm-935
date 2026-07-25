package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.ClientFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.AddressBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.InnBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        PersonBaseFilterPresentationMapper.class,
        UserFilterPresentationMapper.class,
        ClientGroupFilterPresentationMapper.class,
        InnBaseFilterPresentationMapper.class,
        AddressBaseFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface ClientFilterPresentationMapper {

    ClientFilter map(ClientFilterInput input);
}