package com.odilo.interview.job;

import com.odilo.interview.repository.adapter.CacheUserRepositoryAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class CacheInitializationJob {

    private final CacheUserRepositoryAdapter cacheUserRepositoryAdapter;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCache() {
        cacheUserRepositoryAdapter.addAllToCache();
        log.info("[initializeCacheJob] - All users were added todo cache.");
    }
}
