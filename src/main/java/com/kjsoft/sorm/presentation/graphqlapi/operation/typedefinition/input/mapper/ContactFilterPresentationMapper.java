package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ContactFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ContactFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.AddressBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.InnBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
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