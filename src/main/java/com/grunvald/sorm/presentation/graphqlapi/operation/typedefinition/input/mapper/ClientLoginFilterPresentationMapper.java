package com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.mapper;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.ClientLoginFilterInput;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.input.base.mapper.DateTimeBaseFilterPresentationMapper;
import com.grunvald.sorm.service.whmcsdata.filter.ClientLoginFilter;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {
        ClientFilterPresentationMapper.class,
        DateTimeBaseFilterPresentationMapper.class
})
public interface ClientLoginFilterPresentationMapper {

    ClientLoginFilter map(ClientLoginFilterInput filter);
}