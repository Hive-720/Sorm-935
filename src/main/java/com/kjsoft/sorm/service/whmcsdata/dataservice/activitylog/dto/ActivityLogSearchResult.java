package com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.dto;

import com.kjsoft.sorm.service.whmcsdata.dataservice.activitylog.model.ActivityLog;

import java.util.List;


public record ActivityLogSearchResult(String cursor, boolean hasNextPage, List<ActivityLog> activityLogList) {
}