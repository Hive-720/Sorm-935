package com.kjsoft.sorm.presentation.rest.request.mapper;

import com.kjsoft.sorm.business.systemnotification.entity.MetricAlerts;
import com.kjsoft.sorm.presentation.rest.request.MetricAlertsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface MetricAlertsMapper {

    MetricAlertsMapper INSTANCE = Mappers.getMapper(MetricAlertsMapper.class);

    MetricAlerts toEntity(MetricAlertsRequest notify);
}