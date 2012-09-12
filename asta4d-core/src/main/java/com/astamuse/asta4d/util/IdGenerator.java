package com.astamuse.asta4d.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;

public class IdGenerator {

    private final static int randomSeed;
    static {
        // use the last 32 bit of current time as the seed
        ByteBuffer bb = ByteBuffer.allocate(64);
        bb.putLong(System.currentTimeMillis());
        byte[] bytes = bb.array();
        byte[] seed = new byte[4];
        System.arraycopy(bytes, 4, seed, 0, 4);
        SecureRandom sr = new SecureRandom(seed);
        randomSeed = Math.abs(sr.nextInt());
    }

    private final static class IdHolder {
        private long threadId;
        private long lastTime = Long.MIN_VALUE;

        public IdHolder(long threadId) {
            this.threadId = threadId;
        }

        public long getThreadId() {
            return this.threadId;
        }

        public long newTime() {
            long cur = System.currentTimeMillis();
            if (cur > lastTime) {
                lastTime = cur;
            } else {
                lastTime++;
                cur = lastTime;
            }
            return cur;
        }
    }

    private final static ThreadLocal<IdHolder> idHolderCache = new ThreadLocal<IdHolder>() {

        @Override
        protected IdHolder initialValue() {
            return new IdHolder(Thread.currentThread().getId());
        }

    };

    /**
     * a unique id with thread id embedded and a thread unique number
     * 
     * @return
     */
    // TODO we want a fast uuid solution
    public final static String createId() {
        IdHolder idHolder = idHolderCache.get();
        long time = idHolder.newTime();
        Object[] vals = { time, idHolder.getThreadId(), randomSeed };
        return StringUtils.join(vals, "-");
    }
}
