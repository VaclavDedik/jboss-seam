package org.jboss.seam.remoting.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Shane Bryzak
 */
public class SubscriptionRegistry
{
  private static SubscriptionRegistry instance = new SubscriptionRegistry();

  private TopicConnection topicConnection;
  private TopicSession topicSession;

  private Map<String,RemoteSubscriber> subscriptions = new HashMap<String,RemoteSubscriber>();

  private SubscriptionRegistry()
  {
    try {
      InitialContext ctx = new InitialContext();
      TopicConnectionFactory f = (TopicConnectionFactory) ctx.lookup(
          "UIL2ConnectionFactory");

      topicConnection = f.createTopicConnection();
      topicConnection.start();

      topicSession = topicConnection.createTopicSession(false,
            javax.jms.Session.AUTO_ACKNOWLEDGE);
    }
    catch (JMSException ex) {

    }
    catch (NamingException ex) {
    }
  }

  public static SubscriptionRegistry getInstance()
  {
    return instance;
  }

  public RemoteSubscriber subscribe(String topicName)
  {
    RemoteSubscriber sub = new RemoteSubscriber(UUID.randomUUID().toString(), topicName);

    try {
      Topic t = topicSession.createTopic(topicName);
      sub.setTopicSubscriber(topicSession.createSubscriber(t));

      subscriptions.put(sub.getToken(), sub);
      return sub;
    }
    catch (JMSException ex) {
      return null;
    }
  }

  public List<Message> pollNoWait(String token)
  {
    RemoteSubscriber subscriber = subscriptions.get(token);
    if (subscriber == null)
      return null;

    List<Message> messages = null;

    Message m = null;
    do {
      try {
        m = subscriber.getTopicSubscriber().receiveNoWait();
      }
      catch (JMSException ex) {
        ex.printStackTrace();
      }
      if (m != null)
      {
        if (messages == null)
          messages = new ArrayList<Message>();
        messages.add(m);
      }
    }
    while (m != null);

    return messages;
  }

  public List<Message> poll(String token, int timeout)
  {
    RemoteSubscriber subscriber = subscriptions.get(token);
    if (subscriber == null)
      return null;

    List<Message> messages = null;

    Message m = null;
    do {
      try {
        // Only timeout for the first message.. subsequent messages should be nowait
        if (messages == null)
          m = subscriber.getTopicSubscriber().receive(timeout * 1000);
        else
          m = subscriber.getTopicSubscriber().receiveNoWait();
      }
      catch (JMSException ex) {
        ex.printStackTrace();
      }
      if (m != null)
      {
        if (messages == null)
          messages = new ArrayList<Message>();
        messages.add(m);
      }
    }
    while (m != null);

    return messages;
  }
}
