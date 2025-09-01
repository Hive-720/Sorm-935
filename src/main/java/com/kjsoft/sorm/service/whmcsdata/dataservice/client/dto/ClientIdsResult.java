package com.kjsoft.sorm.service.whmcsdata.dataservice.client.dto;

import java.util.List;


public record ClientIdsResult(Long cursorById, boolean hasNextPage, List<Long> clientIds) {
}