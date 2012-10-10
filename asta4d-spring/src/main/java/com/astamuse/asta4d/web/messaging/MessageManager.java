package com.astamuse.asta4d.web.messaging;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.net.URI;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQObjectMessage;

import com.astamuse.asta4d.util.IdGenerator;

public class MessageManager {

    private final static MessageManager MANAGER = new MessageManager();

    private BrokerService broker;
    private TopicConnection connection;
    private TopicSession session;

    public String register(String messageid, Asta4dMessageListener listener) throws JMSException {
        return register(messageid, IdGenerator.createId(), listener);
    }

    public String register(String messageid, String uuid, Asta4dMessageListener listener) throws JMSException {
        if (isEmpty(messageid)) {
            return null;
        }
        MessageConsumer consumer = session.createSubscriber(session.createTopic(messageid));
        consumer.setMessageListener(new ActiveMQMessageListener(consumer, uuid, listener));
        return uuid;
    }

    public void unregister(String messageid, String uuid) throws JMSException {
        Message message = session.createObjectMessage(new UnregisterMessage(uuid));
        TopicPublisher publisher = null;
        try {
            publisher = session.createPublisher(session.createTopic(messageid));
            publisher.publish(message);
        } catch (Exception e) {
            if (publisher != null) {
                publisher.close();
            }
        }
    }

    public void sendMessage(String messageid, Asta4dMessage afdMessage) throws JMSException {
        if (isEmpty(messageid) || afdMessage == null) {
            return;
        }
        Message message = session.createObjectMessage(afdMessage);
        TopicPublisher publisher = null;
        try {
            publisher = session.createPublisher(session.createTopic(messageid));
            publisher.publish(message);
        } catch (Exception e) {
            if (publisher != null) {
                publisher.close();
            }
        }
    }

    private MessageManager() {
        try {
            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI("tcp://localhost:61616"));
            broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(true);
            broker.addConnector(connector);
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

    private static class ActiveMQMessageListener implements MessageListener {
        private final MessageConsumer consumer;
        private final String uuid;
        private final Asta4dMessageListener orgListener;

        private ActiveMQMessageListener(MessageConsumer consumer, String uuid, Asta4dMessageListener orgListener) {
            this.consumer = consumer;
            this.uuid = uuid;
            this.orgListener = orgListener;
        }

        @Override
        public void onMessage(Message message) {
            if (!(message instanceof ActiveMQObjectMessage)) {
                return;
            }
            try {
                ActiveMQObjectMessage mqMsg = (ActiveMQObjectMessage) message;
                String messageid = ((Topic) mqMsg.getDestination()).getTopicName();
                Asta4dMessage afdMsg = (Asta4dMessage) mqMsg.getObject();
                if (afdMsg instanceof UnregisterMessage) {
                    if (orgListener.unregister(messageid, uuid, (UnregisterMessage) afdMsg)) {
                        consumer.close();
                    }
                } else {
                    if (orgListener.onMessage(messageid, afdMsg)) {
                        consumer.close();
                    }
                }
            } catch (JMSException e) {
                // TODO
            }
        }
    }
}
