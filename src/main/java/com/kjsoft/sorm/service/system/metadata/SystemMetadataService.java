package com.kjsoft.sorm.service.system.metadata;

import com.kjsoft.sorm.service.system.notification.model.SystemNotification;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;


public interface SystemMetadataService {

    Mono<Void> checkingAndRestoringIntegrity(@NotNull String component, @NotNull String hash,
                                             @NotNull SystemNotification restoringNotify);
}