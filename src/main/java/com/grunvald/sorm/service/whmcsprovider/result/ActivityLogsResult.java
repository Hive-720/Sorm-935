package com.grunvald.sorm.service.whmcsprovider.result;

import com.grunvald.sorm.service.whmcsprovider.dto.ActivityLogProviderDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ActivityLogsResult {
    private List<ActivityLogProviderDto> elements;
    private boolean hashNextPage;
    private long nextOffset;
}