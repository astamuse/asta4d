package com.astamuse.asta4d.web.messaging;

import javax.jms.MessageListener;

public interface Asta4dMessageListener extends MessageListener {

    boolean closeConsumer();
}
