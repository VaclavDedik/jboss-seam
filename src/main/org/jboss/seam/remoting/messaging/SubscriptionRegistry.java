package org.jboss.seam.remoting.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jms.TopicConnection;

import org.jboss.logging.Logger;
import static org.jboss.seam.InterceptionType.NEVER;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


/**
 *
 * @author Shane Bryzak
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.remoting.messaging.subscriptionRegistry")
public class SubscriptionRegistry
{
  private static final String DEFAULT_CONNECTION_PROVIDER =
    "org.jboss.seam.remoting.messaging.JBossConnectionProvider";

  private Logger log = Logger.getLogger(SubscriptionRegistry.class);

  private String connectionProvider;

  private volatile TopicConnection topicConnection;

  private Object monitor = new Object();

  private Map<String,RemoteSubscriber> subscriptions = new HashMap<String,RemoteSubscriber>();

  public static SubscriptionRegistry instance()
  {
    SubscriptionRegistry registry = (SubscriptionRegistry) Component.getInstance(SubscriptionRegistry.class, true);

    if (registry == null)
    {
      throw new IllegalStateException("No SubscriptionRegistry exists");
    }

    return registry;
  }

  public void setConnectionProvider(String connectionProvider)
  {
    this.connectionProvider = connectionProvider;
  }

  private TopicConnection getTopicConnection()
    throws Exception
  {
    if (topicConnection == null)
    {
      synchronized(monitor)
      {
        if (topicConnection == null)
        {
          String providerName = connectionProvider != null ?
                                    connectionProvider : DEFAULT_CONNECTION_PROVIDER;
          try {
            Class providerClass = Class.forName(providerName);
            JMSConnectionProvider provider = (JMSConnectionProvider) providerClass.newInstance();
            topicConnection = provider.createConnection();
            topicConnection.start();
          }
          catch (ClassNotFoundException ex)
          {
            log.error(String.format("Topic connection provider class [%s] not found",
                                    providerName));
            throw ex;
          }
          catch (InstantiationException ex)
          {
            log.error(String.format("Failed to create connection provider [%s]",
                                    providerName));
            throw ex;
          }
        }
      }
    }
    return topicConnection;
  }

  public RemoteSubscriber subscribe(String topicName)
  {
    RemoteSubscriber sub = new RemoteSubscriber(UUID.randomUUID().toString(), topicName);

    try {
      sub.subscribe(getTopicConnection());
      subscriptions.put(sub.getToken(), sub);
      return sub;
    }
    catch (Exception ex) {
      log.error(ex);
      return null;
    }
  }

  public RemoteSubscriber getSubscription(String token)
  {
    return subscriptions.get(token);
  }
}
