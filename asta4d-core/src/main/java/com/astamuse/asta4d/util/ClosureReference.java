package com.astamuse.asta4d.util;

public class ClosureReference<T> {
    /**
     * This ref is not volatile, which means you should avoid to use it in crossing threads sharing.<br>
     * For cross thread sharing, use {@link SyncClosureReference} instead.
     */
    public T ref = null;
}
