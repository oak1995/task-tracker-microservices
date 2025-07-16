package com.tasktracker.notification.provider;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class NotificationProviderFactory {
    
    private final Map<String, NotificationProvider> providers;
    
    public NotificationProviderFactory(List<NotificationProvider> providerList) {
        this.providers = new HashMap<>();
        providerList.forEach(provider -> providers.put(provider.getChannel(), provider));
    }
    
    public Optional<NotificationProvider> getProvider(String channel) {
        return Optional.ofNullable(providers.get(channel));
    }
    
    public Map<String, NotificationProvider> getAllProviders() {
        return new HashMap<>(providers);
    }
    
    public boolean isChannelSupported(String channel) {
        return providers.containsKey(channel);
    }
} 