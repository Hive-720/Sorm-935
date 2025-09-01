package com.kjsoft.sorm.service.system.metadata;

import com.kjsoft.sorm.service.system.notification.SystemNotificationService;
import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import com.kjsoft.sorm.util.DateUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class SystemMetadataServiceImpl implements SystemMetadataService {
    private final SystemMetadataRepository repository;
    private final SystemNotificationService notificationService;
    private final DateUtils date;


    @Override
    @Transactional
    public Mono<Void> checkingAndRestoringIntegrity(@NotNull String component, @NotNull String hash,
                                                    @Nullable SystemNotification restoringNotify) {
        return repository.findByComponent(component)
                .switchIfEmpty(Mono.just(
                                SystemMetadata.builder()
                                        .component(component)
                                        .hash(hash)
                                        .updatedAt(date.now())
                                        .build()
                        )
                )
                .filter(metadata -> metadata.isNew() || !hash.equals(metadata.getHash()))
                .flatMap(this::saveOrUpdate)
                .filter(metadata -> restoringNotify != null)
                .flatMap(savedMetadata -> notificationService.push(restoringNotify))
                .then();
    }

    private Mono<SystemMetadata> saveOrUpdate(SystemMetadata metadata) {
        return metadata.isNew() ? repository.addMetadata(metadata) : repository.updateMetadata(metadata);
    }
}