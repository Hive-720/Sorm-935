package com.kjsoft.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.kjsoft.sorm.infrastructure.persistence.data.user.entity.relation.UserClientRelationDb;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserClientRelationPersistenceMapper {

    UserClientRelation map(UserClientRelationDb relationDb);

    UserClientRelationDb map(UserClientRelation relation);
}