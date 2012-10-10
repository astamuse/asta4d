package com.astamuse.asta4d.web.messaging;

@SuppressWarnings("serial")
public class UnregisterMessage implements Asta4dMessage {

    private final String uuid;

    public UnregisterMessage(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
