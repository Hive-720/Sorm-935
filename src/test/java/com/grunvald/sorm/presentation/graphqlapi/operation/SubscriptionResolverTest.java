package com.grunvald.sorm.presentation.graphqlapi.operation;

import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.Trap;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.base.DateTimeBase;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.enums.TrapType;
import com.grunvald.sorm.presentation.graphqlapi.operation.typedefinition.type.mapper.TrapMapper;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionResolverTest {

    private SystemNotificationService notificationService;
    private TrapMapper trapMapper;
    private SubscriptionResolver resolver;

    @BeforeEach
    void setUp() {
        notificationService = mock(SystemNotificationService.class);
        trapMapper = mock(TrapMapper.class);
        resolver = new SubscriptionResolver(notificationService, trapMapper);
    }

    @Test
    void trap_mapsEveryNotificationToTrapAndForwardsTheFlux() {
        SystemNotification firstNotification = SystemNotification.builder().message("first").build();
        SystemNotification secondNotification = SystemNotification.builder().message("second").build();
        Trap firstTrap = new Trap(TrapType.MAJORERROR, "first",
                new DateTimeBase("2026-10-24T00:00:00Z"), null);
        Trap secondTrap = new Trap(TrapType.SCHEMCHANGED, "second",
                new DateTimeBase("2026-10-25T00:00:00Z"), null);

        when(notificationService.listen()).thenReturn(Flux.just(firstNotification, secondNotification));
        when(trapMapper.map(firstNotification)).thenReturn(firstTrap);
        when(trapMapper.map(secondNotification)).thenReturn(secondTrap);

        StepVerifier.create(resolver.trap())
                .expectNext(firstTrap)
                .expectNext(secondTrap)
                .verifyComplete();

        verify(trapMapper).map(firstNotification);
        verify(trapMapper).map(secondNotification);
    }

    @Test
    void trap_withEmptyNotificationStream_completesEmptyWithoutMapping() {
        when(notificationService.listen()).thenReturn(Flux.empty());

        StepVerifier.create(resolver.trap())
                .expectNextCount(0)
                .verifyComplete();

        verify(trapMapper, never()).map(any());
    }

    @Test
    void trap_propagatesErrorsFromNotificationStream() {
        RuntimeException upstreamError = new RuntimeException("upstream failed");
        when(notificationService.listen()).thenReturn(Flux.error(upstreamError));

        StepVerifier.create(resolver.trap())
                .expectErrorSatisfies(error -> org.junit.jupiter.api.Assertions.assertSame(upstreamError, error))
                .verify();

        verify(trapMapper, never()).map(any());
    }
}
