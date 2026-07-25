package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.service.whmcsdata.filter.ContactFilter;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ContactFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.AddressBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.InnBaseFilterPresentationMapper;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        PersonBaseFilterPresentationMapper.class,
        AddressBaseFilterPresentationMapper.class,
        InnBaseFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface ContactFilterPresentationMapper {

    ContactFilter map(ContactFilterInput input);
}