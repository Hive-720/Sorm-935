package com.grunvald.sorm.service.whmcsdata.dataservice.user;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.dto.UserSearchResult;
import com.grunvald.sorm.service.whmcsdata.filter.UserFilter;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


public interface UserRepository {

    Mono<Boolean> deleteAllRelations(List<UserClientRelation> relations);

    Mono<Boolean> addAllRelations(List<UserClientRelation> relations);

    Flux<UserClientRelation> findAllRelationsByUserIdIn(Set<Long> userIds);

    Mono<Boolean> addAll(List<User> users);

    Mono<Boolean> updateAll(List<User> users);

    Flux<User> findAllByIdIn(Set<Long> ids);

    Mono<UserSearchResult> findAllByFilter(UserFilter filter, String cursor, Integer offset, Integer limit);
}