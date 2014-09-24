package com.astamuse.asta4d.web.util.timeout;

public interface TimeoutDataManager {

    public <T> T get(String dataId);

    public void put(String dataId, Object data, long expireMilliSeconds);
}
