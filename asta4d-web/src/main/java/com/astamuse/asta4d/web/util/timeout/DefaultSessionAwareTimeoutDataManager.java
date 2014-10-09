/*
 * Copyright 2012 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.astamuse.asta4d.web.util.timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.web.WebApplicationContext;

public class DefaultSessionAwareTimeoutDataManager implements TimeoutDataManager {

    private static final String CheckSessionIdKey = DefaultSessionAwareTimeoutDataManager.class + "#CheckSessionIdKey";

    private final ConcurrentHashMap<String, DataHolder> dataMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            String threadName = "DefaultSessionAwareTimeoutDataManager-Dispose-Thread";
            return new Thread(r, threadName);
        }
    });

    private final int maxDataSize;

    private final boolean sessionAware;

    public DefaultSessionAwareTimeoutDataManager(long expireExcutorPeriodInMinutes, int maxDataSize, boolean sessionAware) {
        this.maxDataSize = maxDataSize;
        this.sessionAware = sessionAware;
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<Entry<String, DataHolder>> entries = new ArrayList<>(dataMap.entrySet());
                long currentTime = System.currentTimeMillis();
                for (Entry<String, DataHolder> entry : entries) {
                    if (entry.getValue().isExpired(currentTime)) {
                        dataMap.remove(entry.getKey());
                    }
                }
            }
        }, expireExcutorPeriodInMinutes, expireExcutorPeriodInMinutes, TimeUnit.MINUTES);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String dataId) {
        DataHolder holder = dataMap.remove(dataId);
        if (holder == null) {
            return null;
        } else if (holder.isExpired(System.currentTimeMillis())) {
            return null;
        } else if (StringUtils.equals(retrieveSessionId(false), holder.sessionId)) {
            return (T) holder.getData();
        } else {
            return null;
        }
    }

    public void put(String dataId, Object data, long expireMilliSeconds) {
        dataMap.putIfAbsent(dataId, new DataHolder(data, expireMilliSeconds, retrieveSessionId(true)));
    }

    protected String retrieveSessionId(boolean create) {
        String sessionId = null;
        if (sessionAware) {
            WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
            sessionId = context.getData(WebApplicationContext.SCOPE_SESSION, CheckSessionIdKey);
            if (sessionId == null && create) {
                sessionId = IdGenerator.createId();
                context.setData(WebApplicationContext.SCOPE_SESSION, CheckSessionIdKey, sessionId);
            }
        }
        return sessionId;
    }

    public void shutdown() {
        service.shutdownNow();
    }

    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

    private static class DataHolder {
        private final Object data;
        private final long creationTime;
        private final long expireMilliSeconds;
        private final String sessionId;

        private DataHolder(Object data, long expireMilliSeconds, String sessionId) {
            this.sessionId = sessionId;
            this.data = data;
            this.expireMilliSeconds = expireMilliSeconds;
            this.creationTime = System.currentTimeMillis();
        }

        private Object getData() {
            return data;
        }

        private boolean isExpired(long currentTime) {
            return (currentTime - creationTime) > expireMilliSeconds;
        }
    }
}
