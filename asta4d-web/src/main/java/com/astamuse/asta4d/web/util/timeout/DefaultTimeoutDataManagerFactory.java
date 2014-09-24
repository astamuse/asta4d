package com.astamuse.asta4d.web.util.timeout;

public class DefaultTimeoutDataManagerFactory implements TimeoutDataManagerFactory {

    private long expireExcutorPeriodInMinutes = 3;

    private int maxDataSize = 1000_000;

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

    @Override
    public TimeoutDataManager create() {
        return new DefaultTimeoutDataManager(expireExcutorPeriodInMinutes, maxDataSize);
    }

}
