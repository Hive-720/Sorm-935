package com.grunvald.sorm.service.whmcsdata.datasyncservice.user;

import com.grunvald.sorm.service.whmcsdata.dataservice.user.UserService;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.User;
import com.grunvald.sorm.service.whmcsdata.dataservice.user.model.UserClientRelation;
import com.grunvald.sorm.service.whmcsprovider.dto.mapper.UserMapper;
import com.grunvald.sorm.service.whmcsprovider.WhmcsProvider;
import com.grunvald.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.ArrayList;


@Service
@RequiredArgsConstructor
@ConditionalOnBean(WhmcsProvider.class)
public class UserSyncServiceImpl implements UserSyncService {
    private final UserService userService;
    private final WhmcsProvider whmcs;
    private final UserMapper userMapper;
    private final DateUtils date;


    @Override
    public Mono<UserSyncPage> getActualData(long offset, int limit) {
        return whmcs.getUsers(offset, limit)
                .flatMap(result -> Flux.fromIterable(result.getElements())
                        .flatMap(user -> Flux.fromIterable(user.getClients())
                                .map(client -> new UserClientRelation(user.getId(), client.getId(), date.now()))
                                .collectList()
                                .map(clientRelations -> Tuples.of(userMapper.map(user), clientRelations))
                        )
                        .collect(
                                () -> Tuples.of(new ArrayList<User>(), new ArrayList<UserClientRelation>()),
                                (tuple2, t2) -> {
                                    tuple2.getT1().add(t2.getT1());
                                    tuple2.getT2().addAll(t2.getT2());
                                }
                        )
                        .map(t2 -> UserSyncPage.builder()
                                .users(t2.getT1())
                                .relations(t2.getT2())
                                .build()
                        )
                );
    }

    @Override
    @Transactional
    public Mono<Boolean> applyActualData(UserSyncPage page) {
        return userService.saveOrUpdateAll(page.getUsers())
                .then(userService.saveOrRemoveRelationsAll(page.getRelations()));
    }
}