package com.astamuse.asta4d.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlashScopeDataManager {

    private final static long EXPIRE_TERM_MILLISEC = 30000;

    private final static long EXECUTE_PERIOD_MINUTES = 5;

    private final static FlashScopeDataManager manager = new FlashScopeDataManager();

    private final ConcurrentHashMap<String, FlashScopeDataHolder> flashScopeMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private FlashScopeDataManager() {
        service.scheduleAtFixedRate(new DataExpireExecutor(), EXECUTE_PERIOD_MINUTES, EXECUTE_PERIOD_MINUTES, TimeUnit.MINUTES);
    }

    Map<String, Object> get(String flashScopeId) {
        FlashScopeDataHolder holder = flashScopeMap.remove(flashScopeId);
        if (holder == null) {
            return Collections.emptyMap();
        }
        return holder.getData();
    }

    void put(String flashScopeId, Map<String, Object> flashScopeData) {
        flashScopeMap.putIfAbsent(flashScopeId, new FlashScopeDataHolder(flashScopeData));
    }

    static FlashScopeDataManager getInstance() {
        return manager;
    }

    private class DataExpireExecutor implements Runnable {
        @Override
        public void run() {
            List<Entry<String, FlashScopeDataHolder>> entries = new ArrayList<>(flashScopeMap.entrySet());
            long currentTime = System.currentTimeMillis();
            for (Entry<String, FlashScopeDataHolder> entry : entries) {
                if (entry.getValue().isExpired(currentTime)) {
                    flashScopeMap.remove(entry.getKey());
                }
            }
        }
    }

    private static class FlashScopeDataHolder {
        private final Map<String, Object> data;
        private final long creationTime;

        private FlashScopeDataHolder(Map<String, Object> data) {
            this.data = data;
            this.creationTime = System.currentTimeMillis();
        }

        private Map<String, Object> getData() {
            return data;
        }

        private boolean isExpired(long currentTime) {
            return (currentTime - creationTime) > EXPIRE_TERM_MILLISEC;
        }
    }
}
