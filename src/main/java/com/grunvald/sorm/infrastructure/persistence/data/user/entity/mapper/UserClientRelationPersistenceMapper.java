package com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.grunvald.sorm.infrastructure.persistence.data.user.entity.relation.UserClientRelationDb;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserClientRelationPersistenceMapper {

    UserClientRelation map(UserClientRelationDb relationDb);

    UserClientRelationDb map(UserClientRelation relation);
}