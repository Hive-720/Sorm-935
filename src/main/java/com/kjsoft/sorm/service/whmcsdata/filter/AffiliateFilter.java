package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AffiliateFilter {
    private ClientFilter client;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;
    private List<AffiliateFilter> and;
    private List<AffiliateFilter> or;
    private AffiliateFilter not;
}