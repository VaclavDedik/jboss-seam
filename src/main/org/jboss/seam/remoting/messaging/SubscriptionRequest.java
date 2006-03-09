package org.jboss.seam.remoting.messaging;

import java.io.OutputStream;
import java.io.IOException;

/**
 *
 * @author Shane Bryzak
 */
public class SubscriptionRequest
{
  private String topicName;
  private RemoteSubscriber subscriber;

  public SubscriptionRequest(String topicName)
  {
    this.topicName = topicName;
  }

  public void subscribe()
  {
    subscriber = SubscriptionRegistry.getInstance().subscribe(topicName);
  }

  public void marshal(OutputStream out)
      throws IOException
  {
    out.write("<subscription topic=\"".getBytes());
    out.write(topicName.getBytes());
    out.write("\" token=\"".getBytes());
    out.write(subscriber.getToken().getBytes());
    out.write("\"/>".getBytes());
    out.flush();
  }
}
