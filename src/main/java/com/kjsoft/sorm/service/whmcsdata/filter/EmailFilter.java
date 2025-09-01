package com.kjsoft.sorm.service.whmcsdata.filter;

import com.kjsoft.sorm.service.whmcsdata.filter.base.DateTimeBaseFilter;
import com.kjsoft.sorm.service.whmcsdata.filter.base.EmailBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class EmailFilter {
    private ClientFilter client;
    private EmailBaseFilter to;
    private EmailBaseFilter cc;
    private EmailBaseFilter bcc;
    private DateTimeBaseFilter date;
    private DateTimeBaseFilter createdAt;
    private DateTimeBaseFilter updatedAt;
    private List<EmailFilter> and;
    private List<EmailFilter> or;
    private EmailFilter not;
}