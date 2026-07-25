package com.grunvald.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.infrastructure.persistence.data.user.entity.UserDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User map(UserDb userDb);

    UserDb map(User userDb);
}