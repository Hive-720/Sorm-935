package com.kjsoft.sorm.infrastructure.persistence.data.user.entity.mapper;

import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;
import com.kjsoft.sorm.infrastructure.persistence.data.user.entity.UserDb;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    User map(UserDb userDb);

    UserDb map(User userDb);
}