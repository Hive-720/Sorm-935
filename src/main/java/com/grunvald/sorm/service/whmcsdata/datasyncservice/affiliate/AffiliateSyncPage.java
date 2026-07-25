package com.grunvald.sorm.service.whmcsdata.datasyncservice.affiliate;

import com.grunvald.sorm.service.whmcsdata.dataservice.affiliate.model.Affiliate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class AffiliateSyncPage {
    private List<Affiliate> elements;
    private boolean hasNextPage;
    private Long nextOffset;
}