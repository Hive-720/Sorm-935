package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.IpAddressBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ClientLoginFilter {
    private ClientFilter client;
    private IpAddressBaseFilter ipAddress;
    private DateTimeBaseFilter date;
    private List<ClientLoginFilter> and;
    private List<ClientLoginFilter> or;
    private ClientLoginFilter not;
}