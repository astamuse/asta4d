package com.astamuse.asta4d.web.messaging;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

public class MessageManager {

    private final static MessageManager MANAGER = new MessageManager();

    private BrokerService broker;
    private TopicConnection connection;
    private TopicSession session;

    public MessageConsumer register(String messageid, Asta4dMessageListener listener) throws JMSException {
        if (isEmpty(messageid)) {
            return null;
        }
        TopicSubscriber subscriber = session.createSubscriber(session.createTopic(messageid));
        subscriber.setMessageListener(new DefaultMessageListener(subscriber, listener));
        return subscriber;
    }

    public void unregister(MessageConsumer consumer) throws JMSException {
        if (consumer == null) {
            return;
        }
        consumer.close();
    }

    public void sendMessage(String messageid, Asta4dMessage message) throws JMSException {
        if (isEmpty(messageid) || message == null) {
            return;
        }
        Message sentMessage = session.createObjectMessage(message);
        TopicPublisher publisher = session.createPublisher(session.createTopic(messageid));
        publisher.publish(sentMessage);
    }

    private MessageManager() {
        try {
            broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(true);
            broker.addConnector("tcp://localhost:61616");
            broker.start();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createTopicConnection();
            connection.setClientID(MessageManager.class.getName());
            connection.start();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (Exception e) {
            // TODO
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (session != null) {
                session.close();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.stop();
                    connection.close();
                }
            } finally {
                if (broker != null && broker.isStarted()) {
                    broker.stop();
                }
            }
        }
    }

    public static MessageManager getInstance() {
        return MANAGER;
    }

    private static class DefaultMessageListener implements MessageListener {
        private final MessageConsumer consumer;
        private final Asta4dMessageListener orgListener;

        private DefaultMessageListener(MessageConsumer consumer, Asta4dMessageListener orgListener) {
            this.consumer = consumer;
            this.orgListener = orgListener;
        }

        @Override
        public void onMessage(Message message) {
            orgListener.onMessage(message);
            try {
                if (orgListener.closeConsumer()) {
                    consumer.close();
                }
            } catch (JMSException e) {
                // TODO
            }
        }
    }
}
