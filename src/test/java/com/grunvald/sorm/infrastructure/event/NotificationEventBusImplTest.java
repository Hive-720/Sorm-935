package com.grunvald.sorm.infrastructure.event;

import com.grunvald.sorm.infrastructure.persistence.systemnotification.SystemNotificationRepository;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventBusImplTest {

    @Mock
    private SystemNotificationRepository repository;

    private NotificationEventBusImpl eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new NotificationEventBusImpl(repository);
    }

    @Test
    void publish_savesAndEmitsToSubscribers() {
        var notification = SystemNotification.builder().message("m").build();
        var saved = SystemNotification.builder().id(1L).message("m").build();

        when(repository.findAllBySentIsFalse()).thenReturn(Flux.empty());
        when(repository.add(any())).thenReturn(Mono.just(saved));
        when(repository.update(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(eventBus.getStream())
                .then(() -> eventBus.publish(notification).block())
                .assertNext(received -> {
                    org.junit.jupiter.api.Assertions.assertEquals(1L, received.getId());
                    org.junit.jupiter.api.Assertions.assertTrue(received.isSent());
                })
                .thenCancel()
                .verify();
    }

    @Test
    void markAsSent_setsFlagAndUpdatesRepository() {
        var notification = SystemNotification.builder().id(2L).message("m").build();
        when(repository.update(any())).thenReturn(Mono.just(notification));

        StepVerifier.create(eventBus.markAsSent(notification))
                .expectNext(notification)
                .verifyComplete();

        org.junit.jupiter.api.Assertions.assertTrue(notification.isSent());
        // sent-флаг должен быть проставлен ДО сохранения, чтобы репозиторий получил обновлённый инстанс
        verify(repository).update(notification);
    }

    @Test
    void getStream_replaysUnsentFromRepository() {
        var unsent = SystemNotification.builder().id(3L).message("unsent").build();
        when(repository.findAllBySentIsFalse()).thenReturn(Flux.just(unsent));
        when(repository.update(any())).thenReturn(Mono.just(unsent));

        StepVerifier.create(eventBus.getStream())
                .assertNext(received -> org.junit.jupiter.api.Assertions.assertEquals(3L, received.getId()))
                .thenCancel()
                .verify();
    }
}
