package com.grunvald.sorm.service.whmcsdata.dataservice.activitylog;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


public interface ActivityLogRepository {

    Mono<OffsetDateTime> getLatestOffsetDateTime();

    Mono<Boolean> addAll(List<ActivityLog> list);

    Mono<ActivityLogSearchResult> findAllByFilter(ActivityLogFilter filter, String cursor, Integer offset, Integer limit);
}