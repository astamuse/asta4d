/*
 * Copyright 2014 astamuse company,Ltd.
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
package com.astamuse.asta4d.web.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.net.util.Base64;

import com.astamuse.asta4d.util.IdGenerator;

public class SecureIdGenerator {

    private final static SecureRandom sr;
    static {
        // use the last 32 bit of current time as the seed
        ByteBuffer bb = ByteBuffer.allocate(64);
        bb.putLong(System.nanoTime());
        byte[] bytes = bb.array();
        byte[] seed = new byte[4];
        System.arraycopy(bytes, 4, seed, 0, 4);
        sr = new SecureRandom(seed);
    }

    public static String createEncryptedURLSafeId() {
        try {
            String uuid = IdGenerator.createId();

            byte[] idBytes = uuid.getBytes();
            ByteBuffer bb = ByteBuffer.allocate(idBytes.length + 4);
            bb.put(idBytes);

            // add random salt
            bb.putInt(sr.nextInt());

            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            return Base64.encodeBase64URLSafeString(crypt.digest(bb.array()));

        } catch (NoSuchAlgorithmException e) {
            // impossible
            throw new RuntimeException(e);
        }
    }
}
