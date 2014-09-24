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
import java.util.concurrent.TimeUnit;

public class DefaultTimeoutDataManager implements TimeoutDataManager {

    private final ConcurrentHashMap<String, DataHolder> dataMap = new ConcurrentHashMap<>();

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private final int maxDataSize;

    public DefaultTimeoutDataManager(long expireExcutorPeriodInMinutes, int maxDataSize) {
        this.maxDataSize = maxDataSize;
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
        } else {
            return (T) holder.getData();
        }
    }

    public void put(String dataId, Object data, long expireMilliSeconds) {
        dataMap.putIfAbsent(dataId, new DataHolder(data, expireMilliSeconds));
    }

    public void shutdown() {
        service.shutdown();
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

        private DataHolder(Object data, long expireMilliSeconds) {
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
