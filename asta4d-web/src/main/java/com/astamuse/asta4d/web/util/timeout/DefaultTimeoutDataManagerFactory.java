package com.astamuse.asta4d.web.util.timeout;

public class DefaultTimeoutDataManagerFactory implements TimeoutDataManagerFactory {

    private long expireExcutorPeriodInMinutes = 3;

    private int maxDataSize = 1000_000;

    private boolean sessionAware = true;

    public long getExpireExcutorPeriodInMinutes() {
        return expireExcutorPeriodInMinutes;
    }

    public void setExpireExcutorPeriodInMinutes(long expireExcutorPeriodInMinutes) {
        this.expireExcutorPeriodInMinutes = expireExcutorPeriodInMinutes;
    }

    public int getMaxDataSize() {
        return maxDataSize;
    }

    public void setMaxDataSize(int maxDataSize) {
        this.maxDataSize = maxDataSize;
    }

    public boolean isSessionAware() {
        return sessionAware;
    }

    public void setSessionAware(boolean sessionAware) {
        this.sessionAware = sessionAware;
    }

    @Override
    public TimeoutDataManager create() {
        return new DefaultSessionAwareTimeoutDataManager(expireExcutorPeriodInMinutes, maxDataSize, sessionAware);
    }

}
