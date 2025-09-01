package com.kjsoft.sorm.service.whmcsdata.dataservice.user;

import com.kjsoft.sorm.service.whmcsdata.dataservice.client.model.Client;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.User;
import com.kjsoft.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import com.kjsoft.sorm.service.whmcsdata.filter.UserFilter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserService {

    Mono<List<User>> getAllByIds(Set<Long> ids);

    Mono<Boolean> saveOrUpdateAll(List<User> users);

    Mono<Boolean> saveOrRemoveRelationsAll(List<UserClientRelation> relations);

    Mono<Map<Long, List<Client>>> getGroupingClientsByUser(Set<Long> userIds);

    Mono<UserSearchResult> search(UserFilter filter, String cursor, Integer offset, Integer limit);
}