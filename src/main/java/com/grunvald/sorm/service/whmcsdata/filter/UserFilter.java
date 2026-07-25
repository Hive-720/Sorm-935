package com.grunvald.sorm.service.whmcsdata.filter;

import com.grunvald.sorm.service.whmcsdata.filter.base.EmailBaseFilter;
import com.grunvald.sorm.service.whmcsdata.filter.base.PersonBaseFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserFilter {
    private PersonBaseFilter name;
    private EmailBaseFilter email;
    private List<UserFilter> and;
    private List<UserFilter> or;
    private UserFilter not;
}