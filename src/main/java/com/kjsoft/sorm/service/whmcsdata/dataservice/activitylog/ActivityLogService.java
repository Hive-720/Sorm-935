package com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog;

import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.kjsoft.sorm.service.whmcsdata.filter.ActivityLogFilter;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


public interface ActivityLogService {

    Mono<OffsetDateTime> getLatestOffsetDateTime();

    Mono<Boolean> saveAll(List<ActivityLog> list);

    Mono<ActivityLogSearchResult> search(ActivityLogFilter filter, String cursor, Integer offset, Integer limit);
}