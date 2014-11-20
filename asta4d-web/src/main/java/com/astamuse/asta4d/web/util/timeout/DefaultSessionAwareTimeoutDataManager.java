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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.util.IdGenerator;
import com.astamuse.asta4d.web.WebApplicationContext;

public class DefaultSessionAwareTimeoutDataManager implements TimeoutDataManager {

    private static final String CheckSessionIdKey = DefaultSessionAwareTimeoutDataManager.class + "#CheckSessionIdKey";

    private ConcurrentHashMap<String, DataHolder> dataMap = null;

    private AtomicInteger dataCounter = null;

    private ScheduledExecutorService service = null;

    // 3 minutes
    private long expirationCheckPeriodInMilliseconds = 3 * 60 * 1000;

    private int maxDataSize = 10_000;

    private boolean sessionAware = true;

    private long spinTimeInMilliseconds = 1000;

    // 5 times spinning
    private long maxSpinTimeInMilliseconds = 1000 * 5;

    private String checkThreadName = this.getClass().getSimpleName() + "-check-thread";

    public DefaultSessionAwareTimeoutDataManager() {

    }

    public void setExpirationCheckPeriodInMilliseconds(long expirationCheckPeriodInMilliseconds) {
        this.expirationCheckPeriodInMilliseconds = expirationCheckPeriodInMilliseconds;
    }

    public void setMaxDataSize(int maxDataSize) {
        this.maxDataSize = maxDataSize;
    }

    public void setSessionAware(boolean sessionAware) {
        this.sessionAware = sessionAware;
    }

    public void setSpinTimeInMilliseconds(long spinTimeInMilliseconds) {
        this.spinTimeInMilliseconds = spinTimeInMilliseconds;
    }

    public void setMaxSpinTimeInMilliseconds(long maxSpinTimeInMilliseconds) {
        this.maxSpinTimeInMilliseconds = maxSpinTimeInMilliseconds;
    }

    @Override
    public void start() {
        // init fields
        dataMap = new ConcurrentHashMap<>();
        dataCounter = new AtomicInteger();
        service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, checkThreadName);
            }
        });

        // start check thread
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<Entry<String, DataHolder>> entries = new ArrayList<>(dataMap.entrySet());
                long currentTime = System.currentTimeMillis();
                int removedCounter = 0;
                Object existing;
                for (Entry<String, DataHolder> entry : entries) {
                    if (entry.getValue().isExpired(currentTime)) {
                        existing = dataMap.remove(entry.getKey());
                        if (existing != null) {// we removed it successfully
                            removedCounter++;
                        }
                    }
                }
                if (removedCounter > 0) {
                    dataCounter.addAndGet(-removedCounter);
                }
            }
        }, expirationCheckPeriodInMilliseconds, expirationCheckPeriodInMilliseconds, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        // release all resources
        service.shutdownNow();
        dataCounter = null;
        dataMap = null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String dataId) {
        DataHolder holder = dataMap.remove(dataId);
        if (holder == null) {
            return null;
        } else {
            dataCounter.decrementAndGet();
            if (holder.isExpired(System.currentTimeMillis())) {
                return null;
            } else if (StringUtils.equals(retrieveSessionId(false), holder.sessionId)) {
                return (T) holder.getData();
            } else {
                return null;
            }
        }
    }

    public void put(String dataId, Object data, long expireMilliSeconds) {
        if (dataCounter.get() >= maxDataSize) {
            try {
                long spinTimeTotal = 0;
                while (dataCounter.get() >= maxDataSize) {
                    if (spinTimeTotal >= maxSpinTimeInMilliseconds) {
                        String msg = "There are too many data in %s and we could not get empty space after waiting for %d milliseconds."
                                + " The configured max size is %d and perhaps you should increase the value.";
                        msg = String.format(msg, this.getClass().getName(), spinTimeTotal, maxDataSize);
                        throw new TooManyDataException(msg);
                    }
                    Thread.sleep(spinTimeInMilliseconds);
                    spinTimeTotal += spinTimeInMilliseconds;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Object existing = dataMap.putIfAbsent(dataId, new DataHolder(data, expireMilliSeconds, retrieveSessionId(true)));
        if (existing == null) {
            dataCounter.incrementAndGet();
        }
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

    @Override
    protected void finalize() throws Throwable {
        stop();
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
