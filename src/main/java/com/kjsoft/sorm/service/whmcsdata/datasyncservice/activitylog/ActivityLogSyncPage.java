package com.kjsoft.sorm.service.whmcsdata.datasyncservice.activitylog;

import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class ActivityLogSyncPage {
   private List<ActivityLog> elements;
   private boolean hasNextPage;
   private Long nextOffset;
}