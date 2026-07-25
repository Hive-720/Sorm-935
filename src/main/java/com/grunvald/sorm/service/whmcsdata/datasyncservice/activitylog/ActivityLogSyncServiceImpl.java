package com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog;

import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ActivityLogMapper;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.ActivityLogService;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class ActivityLogSyncServiceImpl implements ActivityLogSyncService {
    private final ActivityLogService activityLogService;
    private final ActivityLogMapper activityLogMapper;
    private final WhmcsProvider whmcs;


    @Override
    public Mono<OffsetDateTime> getLatestCursor() {
        return activityLogService.getLatestOffsetDateTime();
    }

    @Override
    public Mono<ActivityLogSyncPage> getActualData(long offset, int limit, OffsetDateTime minDate,
                                                   List<String> filterTemplates) {
        return whmcs.getActivityLogs(offset, limit, minDate)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .filter(activity -> filterByDescription(filterTemplates, activity.getDescription()))
                        .map(activityLogMapper::map)
                        .collectList()
                        .map(list -> ActivityLogSyncPage.builder()
                                .elements(list)
                                .hasNextPage(result.isHashNextPage())
                                .nextOffset(result.getNextOffset())
                                .build()
                        )
                );
    }

    @Override
    public Mono<Boolean> applyActualData(ActivityLogSyncPage data) {
        return activityLogService.saveAll(data.getElements());
    }

    private boolean filterByDescription(List<String> filterTemplates, String description) {
        return filterTemplates.stream()
                .noneMatch(description::matches);
    }
}