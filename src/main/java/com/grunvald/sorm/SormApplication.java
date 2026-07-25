package com.grunvald.sorm;

import com.grunvald.sorm.config.NotificationPropertiesConfig;
import com.grunvald.sorm.service.system.notification.SystemNotificationService;
import com.grunvald.sorm.service.system.notification.model.SystemNotification;
import com.grunvald.sorm.service.system.notification.model.Type;
import com.grunvald.sorm.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;


@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class SormApplication {
    private final SystemNotificationService notificationService;
    private final BuildProperties buildProperties;
    private final DateUtils date;
    private final NotificationPropertiesConfig notificationConfig;


    public static void main(String[] args) {
        Hooks.enableAutomaticContextPropagation();
        SpringApplication.run(SormApplication.class, args);
    }

    @Bean
    public CommandLineRunner startupRunner() {
        return args -> {
            if (notificationConfig.isReload()) {
                pushStartAppNotification()
                        .doOnError(e -> log.error("Failed to send rebooted notification", e))
                        .subscribe();
            }
        };
    }

    private Mono<Void> pushStartAppNotification() {
        var appVersion = buildProperties.getVersion();
        var appName = buildProperties.getName();
        return notificationService.push(SystemNotification.builder()
                .message(appName + "/" + appVersion + " has been rebooted")
                .createdAt(date.now())
                .type(Type.RESTARTDB)
                .build()
        );
    }
}