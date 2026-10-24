package com.grunvald.sorm.service.whmcsdata.datasyncservice.activitylog;

import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.ActivityLogService;
import com.grunvald.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.ActivityLogMapper;
import com.grunvald.sorm.service.whmcsprovider.result.ActivityLogsResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogSyncServiceImplTest {

    private static final OffsetDateTime MIN_DATE = OffsetDateTime.parse("2026-01-01T00:00:00Z");

    @Mock
    private ActivityLogService activityLogService;
    @Mock
    private ActivityLogMapper activityLogMapper;
    @Mock
    private WhmcsProvider whmcs;

    private ActivityLogSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ActivityLogSyncServiceImpl(activityLogService, activityLogMapper, whmcs);
    }

    private ActivityLogProviderDto dto(long id, String description) {
        var dto = new ActivityLogProviderDto();
        dto.setId(id);
        dto.setDescription(description);
        return dto;
    }

    private ActivityLog log(long id) {
        var l = new ActivityLog();
        l.setId(id);
        return l;
    }

    @Test
    void getLatestCursor_delegatesToActivityLogService() {
        var latest = OffsetDateTime.parse("2026-02-01T00:00:00Z");
        when(activityLogService.getLatestOffsetDateTime()).thenReturn(Mono.just(latest));

        StepVerifier.create(service.getLatestCursor())
                .expectNext(latest)
                .verifyComplete();
    }

    @Test
    void getActualData_mapsAndFiltersByDescription() {
        var noisy = dto(1L, "Cron Job: cache update");
        var clean = dto(2L, "Admin login");
        var mapped = log(2L);

        when(whmcs.getActivityLogs(0L, 10, MIN_DATE))
                .thenReturn(Mono.just(ActivityLogsResult.builder()
                        .elements(List.of(noisy, clean))
                        .hashNextPage(false)
                        .nextOffset(0L)
                        .build()));
        when(activityLogMapper.map(clean)).thenReturn(mapped);

        StepVerifier.create(service.getActualData(0L, 10, MIN_DATE, List.of("Cron Job:.*")))
                .assertNext(page -> {
                    assertEquals(List.of(mapped), page.getElements(), "логи, подходящие под шаблон, отбрасываются");
                    assertFalse(page.isHasNextPage());
                })
                .verifyComplete();

        verify(activityLogMapper, never()).map(noisy);
    }

    @Test
    void getActualData_nullTemplates_noFiltering() {
        var dto1 = dto(1L, "Cron Job: cache update");
        var dto2 = dto(2L, "Admin login");
        var log1 = log(1L);
        var log2 = log(2L);

        when(whmcs.getActivityLogs(0L, 10, MIN_DATE))
                .thenReturn(Mono.just(ActivityLogsResult.builder()
                        .elements(List.of(dto1, dto2))
                        .hashNextPage(true)
                        .nextOffset(10L)
                        .build()));
        when(activityLogMapper.map(dto1)).thenReturn(log1);
        when(activityLogMapper.map(dto2)).thenReturn(log2);

        StepVerifier.create(service.getActualData(0L, 10, MIN_DATE, null))
                .assertNext(page -> {
                    assertEquals(List.of(log1, log2), page.getElements());
                    assertTrue(page.isHasNextPage());
                    assertEquals(10L, page.getNextOffset());
                })
                .verifyComplete();
    }

    @Test
    void getActualData_nullDescription_kept() {
        var dto = dto(1L, null);
        var mapped = log(1L);

        when(whmcs.getActivityLogs(0L, 10, MIN_DATE))
                .thenReturn(Mono.just(ActivityLogsResult.builder()
                        .elements(List.of(dto))
                        .hashNextPage(false)
                        .nextOffset(0L)
                        .build()));
        when(activityLogMapper.map(dto)).thenReturn(mapped);

        StepVerifier.create(service.getActualData(0L, 10, MIN_DATE, List.of(".*")))
                .assertNext(page -> assertEquals(List.of(mapped), page.getElements()))
                .verifyComplete();
    }

    @Test
    void applyActualData_delegatesToActivityLogService() {
        var page = ActivityLogSyncPage.builder().elements(List.of(log(1))).build();
        when(activityLogService.saveAll(page.getElements())).thenReturn(Mono.just(true));

        StepVerifier.create(service.applyActualData(page))
                .expectNext(true)
                .verifyComplete();
    }
}
