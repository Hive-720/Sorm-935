package com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog;

import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


public interface ActivityLogSyncService {

    Mono<OffsetDateTime> getLatestCursor();

    Mono<ActivityLogSyncPage> getActualData(long offset, int limit, OffsetDateTime minDate, List<String> filterTemplates);

    Mono<Boolean> applyActualData(ActivityLogSyncPage data);
}