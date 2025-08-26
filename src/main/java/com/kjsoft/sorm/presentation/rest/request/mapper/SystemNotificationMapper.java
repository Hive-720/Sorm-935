package com.kjsoft.sorm.presentation.rest.request.mapper;

import com.kjsoft.sorm.business.systemnotification.entity.SystemNotification;
import com.kjsoft.sorm.presentation.rest.request.NotificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface SystemNotificationMapper {

    SystemNotificationMapper INSTANCE = Mappers.getMapper(SystemNotificationMapper.class);

    SystemNotification toEntity(NotificationRequest notify);
}