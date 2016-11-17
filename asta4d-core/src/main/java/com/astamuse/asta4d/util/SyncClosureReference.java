package com.astamuse.asta4d.util;

public class SyncClosureReference<T> {
    /**
     * This ref is volatile, which is necessary for make sure all threads can retrieve the reference after it has been assigned. Further,
     * since we will write it once and read it many times in most situations, the cost is payable.
     */
    public volatile T ref = null;
}
