package com.grunvald.sorm.presentation.graphqlapi.operation;

import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Trap;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TrapMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;


@Controller
@RequiredArgsConstructor
public class SubscriptionResolver {
    private final SystemNotificationService systemNotificationService;
    private final TrapMapper trapMapper;


    @SubscriptionMapping
    public Flux<Trap> trap() {
        return systemNotificationService.listen()
                .map(trapMapper::map);
    }
}