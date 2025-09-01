package com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.kjsoft.sorm.service.whmcsdata.filter.ClientFilter;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientFilterInput;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.AddressBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.InnBaseFilterPresentationMapper;
import com.kjsoft.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.PersonBaseFilterPresentationMapper;
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