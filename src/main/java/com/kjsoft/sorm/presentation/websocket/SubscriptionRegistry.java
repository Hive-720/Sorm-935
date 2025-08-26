package com.kjsoft.sorm.presentation.websocket;

import org.springframework.stereotype.Component;
import reactor.core.Disposable;

import java.util.concurrent.ConcurrentHashMap;


@Component
public class SubscriptionRegistry {
    //TODO поставить Optional вместо disposable?
    private final ConcurrentHashMap<String, Disposable> subscriptions = new ConcurrentHashMap<>();

    public boolean isExist(String id) {
        return subscriptions.containsKey(id);
    }

    public void add(String id, Disposable subscription) {
        subscriptions.put(id, subscription);
    }

    public void cancel(String id) {
        Disposable sub = subscriptions.remove(id);
        if (sub != null && !sub.isDisposed()) {
            sub.dispose();
        }
    }

    public void cancelAll() {
        subscriptions.forEach((sub, disposable) -> disposable.dispose());
    }
}