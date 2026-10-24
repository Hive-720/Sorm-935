package com.grunvald.sorm.service.whmcsdata.dataservice.activitylog;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.dto.ActivityLogSearchResult;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.grunvald.sorm.service.whmcsdata.filter.ActivityLogFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceImplTest {

    @Mock
    private ActivityLogRepository repository;

    private ActivityLogServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ActivityLogServiceImpl(repository);
    }

    @Test
    void getLatestOffsetDateTime_delegatesToRepository() {
        var latest = OffsetDateTime.parse("2026-01-01T00:00:00Z");
        when(repository.getLatestOffsetDateTime()).thenReturn(Mono.just(latest));

        StepVerifier.create(service.getLatestOffsetDateTime())
                .expectNext(latest)
                .verifyComplete();
    }

    @Test
    void saveAll_delegatesToRepository() {
        var logs = List.of(new ActivityLog());
        when(repository.addAll(logs)).thenReturn(Mono.just(true));

        StepVerifier.create(service.saveAll(logs))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void search_delegatesToRepository() {
        var filter = new ActivityLogFilter();
        var result = new ActivityLogSearchResult("c", false, List.of());
        when(repository.findAllByFilter(filter, "c", 0, 25)).thenReturn(Mono.just(result));

        StepVerifier.create(service.search(filter, "c", 0, 25))
                .expectNext(result)
                .verifyComplete();
    }
}
