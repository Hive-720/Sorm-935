package com.kjsoft.sorm.presentation.api.request.mapper;

import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.presentation.api.request.NotificationRequest;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.IGNORE;


@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface SystemNotificationPresentationMapper {

    SystemNotification map(NotificationRequest notify);
}