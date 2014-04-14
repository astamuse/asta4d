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

package com.astamuse.asta4d.web.messaging;


public class MessageManager {

    /*
    private final static MessageManager MANAGER = new MessageManager();

    private BrokerService broker;
    private TopicConnection connection;
    private TopicSession session;

    public String register(String messageid, Asta4dMessageListener listener) throws JMSException {
        return register(messageid, IdGenerator.createId(), listener);
    }

    public String register(String messageid, String uuid, Asta4dMessageListener listener) throws JMSException {
        if (isEmpty(messageid) || isEmpty(uuid) || listener == null) {
            throw new IllegalArgumentException();
        }
        MessageConsumer consumer = session.createSubscriber(session.createTopic(messageid));
        consumer.setMessageListener(new ActiveMQMessageListener(consumer, uuid, listener));
        return uuid;
    }

    public void unregister(String messageid, String uuid) throws JMSException {
        if (isEmpty(messageid) || isEmpty(uuid)) {
            throw new IllegalArgumentException();
        }
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
            throw new IllegalArgumentException();
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
                    UnregisterMessage unregisterMessage = (UnregisterMessage) afdMsg;
                    if (!uuid.equals(unregisterMessage.getUuid())) {
                        return;
                    }
                    if (orgListener.unregister(messageid, unregisterMessage)) {
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
    */
}
