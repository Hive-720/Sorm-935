package com.kjsoft.sorm.service.whmcsdata.datasyncservice.user;

import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class UserSyncPage {
    private List<User> users;
    private List<UserClientRelation> relations;
    private boolean hasNextPage;
    private Long nextOffset;
}