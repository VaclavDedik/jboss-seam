package org.jboss.seam.remoting.messaging;

import java.util.List;
import javax.jms.Message;

/**
 *
 * @author Shane Bryzak
 */
public class PollRequest
{
  private String token;
  private int timeout;
  private List<Message> messages;

  public PollRequest(String token, int timeout)
  {
    this.token = token;
    this.timeout = timeout;
  }

  public String getToken()
  {
    return token;
  }

  public List<Message> getMessages()
  {
    return messages;
  }

  public void poll()
  {
    if (timeout > 0)
      messages = SubscriptionRegistry.getInstance().poll(token, timeout);
    else
      messages = SubscriptionRegistry.getInstance().pollNoWait(token);
  }
}
