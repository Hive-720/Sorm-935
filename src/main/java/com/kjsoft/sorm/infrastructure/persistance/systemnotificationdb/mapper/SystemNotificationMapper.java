package com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb.mapper;

import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import com.kjsoft.sorm.infrastructure.persistance.systemnotificationdb.SystemNotificationDB;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SystemNotificationMapper {

    SystemNotificationMapper INSTANCE = Mappers.getMapper(SystemNotificationMapper.class);

    SystemNotification toEntity(SystemNotificationDB notify);

    SystemNotificationDB toEntityDb(SystemNotification notify);
}