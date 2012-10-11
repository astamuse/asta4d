package com.astamuse.asta4d.web.messaging;

public interface Asta4dMessageListener {

    boolean onMessage(String messageid, Asta4dMessage message);

    boolean unregister(String messageid, UnregisterMessage message);
}
