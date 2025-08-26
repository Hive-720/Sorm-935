package com.kjsoft.sorm.presentation.graphql.subscription;

import com.kjsoft.sorm.business.systemnotification.SystemNotificationService;
import com.kjsoft.sorm.presentation.graphql.subscription.enums.TrapType;
import com.kjsoft.sorm.presentation.graphql.subscription.records.Trap;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;


@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final SystemNotificationService systemNotificationService;

    //TODO в доке не видел обратную связь о доставке,
    //todo в случае падения сервиса выгрузим все что незавершенное и забросим в sink
    @SubscriptionMapping
    public Flux<Trap> trap() {
        System.out.println("connected: " + System.currentTimeMillis());
        return systemNotificationService.listen()
                .map(notify -> new Trap(TrapType.MINORERROR, "test msg"));
//                .map(notify -> {
//                            var extensions = new HashMap<>();
//                            extensions.put("notification", notify);
//                            return ExecutionResult.newExecutionResult()
//                                    .data(new Trap(TrapType.MINORERROR, "test msg"))
//                                    .extensions(extensions)
//                                    .build();
//                        }
//                );
    }

//    @SubscriptionMapping
//    public Flux<String> statusOfflineRequest(@Argument String requestID) {
//        return Flux.interval(Duration.ofSeconds(1))
//                .map(r -> UUID.randomUUID().toString());
//    }
}