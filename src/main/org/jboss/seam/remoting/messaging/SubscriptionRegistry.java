package org.jboss.seam.remoting.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.jboss.logging.Logger;

/**
 *
 * @author Shane Bryzak
 */
public class SubscriptionRegistry
{
  private static Logger log = Logger.getLogger(SubscriptionRegistry.class);

  private static SubscriptionRegistry instance = new SubscriptionRegistry();

  private TopicConnection topicConnection;

  private Map<String,RemoteSubscriber> subscriptions = new HashMap<String,RemoteSubscriber>();

  private SubscriptionRegistry()
  {
    try {
      InitialContext ctx = new InitialContext();
      TopicConnectionFactory f = (TopicConnectionFactory) ctx.lookup(
          "UIL2ConnectionFactory");

      topicConnection = f.createTopicConnection();
      topicConnection.start();
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
      sub.subscribe(topicConnection);
      subscriptions.put(sub.getToken(), sub);
      return sub;
    }
    catch (JMSException ex) {
      log.error(ex);
      return null;
    }
  }

  public RemoteSubscriber getSubscription(String token)
  {
    return subscriptions.get(token);
  }
}
