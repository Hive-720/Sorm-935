package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.base.IpAddressBaseFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.base.LoginBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ActivityLogFilter {
    private ClientFilter client;
    private UserFilter user;
    private LoginBaseFilter username;
    private IpAddressBaseFilter ipAddress;
    private DateTimeBaseFilter date;
    private List<ActivityLogFilter> and;
    private List<ActivityLogFilter> or;
    private ActivityLogFilter not;
}