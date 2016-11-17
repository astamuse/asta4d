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

package com.astamuse.asta4d.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

public class IdGenerator {

    private static Encoder b64Encoder = Base64.getUrlEncoder();

    // this seed must by 5 bytes due to avoid base64 padding later
    private final static byte[] randomSeed;

    static {
        // use the last 32 bit of current time as the seed
        ByteBuffer bb = ByteBuffer.allocate(64);
        bb.putLong(System.currentTimeMillis());
        byte[] bytes = bb.array();
        byte[] seed = new byte[4];
        System.arraycopy(bytes, 4, seed, 0, 4);

        SecureRandom sr = new SecureRandom(seed);
        int s = Math.abs(sr.nextInt());

        ByteBuffer seedBuffer = ByteBuffer.allocate(5);
        seedBuffer.putInt(s);
        seedBuffer.put(seed[3]);

        randomSeed = seedBuffer.array();
    }

    private final static class IdHolder {

        // must be times of 3 to avoid padding, 8 + 8 + 5 = 21
        private ByteBuffer buffer = ByteBuffer.allocate(21);

        private long threadId;
        private long lastTime = Long.MIN_VALUE;

        public IdHolder(long threadId) {
            this.threadId = threadId;
            this.buffer.mark();
        }

        public long getThreadId() {
            return this.threadId;
        }

        long newTime() {
            // since the current milliseconds is less than 40 bit, we think this
            // operation is safe
            long cur = System.currentTimeMillis() << 7;
            if (cur > lastTime) {
                lastTime = cur;
            } else {
                lastTime++;
                cur = lastTime;
            }
            return cur;
        }

        public byte[] newId() {
            buffer.reset();
            buffer.putLong(newTime());// 8
            buffer.putLong(threadId);// 8
            buffer.put(randomSeed);// 5
            byte[] bs = new byte[21];
            System.arraycopy(buffer.array(), 0, bs, 0, 21);
            return bs;
        }
    }

    private final static ThreadLocal<IdHolder> idHolderCache = new ThreadLocal<IdHolder>() {

        @Override
        protected IdHolder initialValue() {
            return new IdHolder(Thread.currentThread().getId());
        }

    };

    /**
     * a unique id with thread id embedded and a process unique(random) number, as string.
     * 
     * @return
     */
    public final static String createId() {
        IdHolder idHolder = idHolderCache.get();
        return b64Encoder.encodeToString(idHolder.newId());
    }

    /**
     * a unique id with thread id embedded and a process unique(random) number, as byte array
     * 
     * @return
     */
    public final static byte[] createIdBytes() {
        IdHolder idHolder = idHolderCache.get();
        return idHolder.newId();
    }
}
