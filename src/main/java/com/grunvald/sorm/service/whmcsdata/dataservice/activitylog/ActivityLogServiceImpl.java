package com.grunvald.sorm.service.whmcsdata.dataservice.activitylog;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {
    private final ActivityLogRepository repository;


    @Override
    public Mono<OffsetDateTime> getLatestOffsetDateTime() {
        return repository.getLatestOffsetDateTime();
    }

    @Override
    public Mono<Boolean> saveAll(List<ActivityLog> activityLogs) {
        return repository.addAll(activityLogs);
    }

    @Override
    public Mono<ActivityLogSearchResult> search(ActivityLogFilter filter, String cursor,
                                                Integer offset, Integer limit) {
        return repository.findAllByFilter(filter, cursor, offset, limit);
    }
}