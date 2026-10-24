package com.grunvald.sorm.service.system.metadata;

import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.util.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemMetadataServiceImplTest {

    private static final String COMPONENT = "GRAPHQL_SCHEMA";

    @Mock
    private SystemMetadataRepository repository;
    @Mock
    private SystemNotificationService notificationService;
    @Mock
    private DateUtils date;

    @Captor
    private ArgumentCaptor<SystemMetadata> metadataCaptor;

    private SystemMetadataServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SystemMetadataServiceImpl(repository, notificationService, date);
    }

    @Test
    void firstRun_createsRecord_withoutNotification() {
        var now = OffsetDateTime.parse("2026-01-02T03:04:05Z");
        when(date.now()).thenReturn(now);
        when(repository.findByComponent(COMPONENT)).thenReturn(Mono.empty());
        when(repository.addMetadata(any())).thenAnswer(inv -> {
            var metadata = inv.getArgument(0, SystemMetadata.class);
            return Mono.just(SystemMetadata.builder()
                    .id(1L)
                    .component(metadata.getComponent())
                    .hash(metadata.getHash())
                    .build());
        });

        StepVerifier.create(service.checkingAndRestoringIntegrity(COMPONENT, "hash1", null))
                .verifyComplete();

        verify(repository).addMetadata(metadataCaptor.capture());
        var saved = metadataCaptor.getValue();
        assertEquals(COMPONENT, saved.getComponent());
        assertEquals("hash1", saved.getHash());
        assertEquals(now, saved.getUpdatedAt(), "новая запись должна получить updatedAt от date.now()");
        verify(repository, never()).updateMetadata(any());
        verify(notificationService, never()).push(any());
    }

    @Test
    void hashUnchanged_nothingSaved() {
        var existing = SystemMetadata.builder().id(1L).component(COMPONENT).hash("hash1").build();
        when(repository.findByComponent(COMPONENT)).thenReturn(Mono.just(existing));

        StepVerifier.create(service.checkingAndRestoringIntegrity(COMPONENT, "hash1", null))
                .verifyComplete();

        verify(repository, never()).addMetadata(any());
        verify(repository, never()).updateMetadata(any());
    }

    @Test
    void hashChanged_updatesRecord_andPushesNotification() {
        var existing = SystemMetadata.builder().id(1L).component(COMPONENT).hash("old").build();
        var notify = SystemNotification.builder().message("changed").build();

        when(repository.findByComponent(COMPONENT)).thenReturn(Mono.just(existing));
        when(repository.updateMetadata(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(notificationService.push(notify)).thenReturn(Mono.empty());

        StepVerifier.create(service.checkingAndRestoringIntegrity(COMPONENT, "new", notify))
                .verifyComplete();

        verify(repository).updateMetadata(any());
        verify(notificationService).push(notify);
    }

    @Test
    void hashChanged_nullNotification_noPush() {
        var existing = SystemMetadata.builder().id(1L).component(COMPONENT).hash("old").build();

        when(repository.findByComponent(COMPONENT)).thenReturn(Mono.just(existing));
        when(repository.updateMetadata(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(service.checkingAndRestoringIntegrity(COMPONENT, "new", null))
                .verifyComplete();

        verify(notificationService, never()).push(any());
    }
}
