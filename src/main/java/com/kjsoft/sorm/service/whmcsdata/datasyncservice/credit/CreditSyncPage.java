package com.kjsoft.sorm.service.whmcsdata.datasyncservice.credit;

import com.kjsoft.sorm.service.whmcsdata.dataservice.credit.model.Credit;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class CreditSyncPage {
    private List<Credit> credits;
    private boolean hasNextPage;
    private Long cursor;

    public static final CreditSyncPage EMPTY = CreditSyncPage.builder()
            .credits(List.of())
            .hasNextPage(false)
            .cursor(null)
            .build();
}