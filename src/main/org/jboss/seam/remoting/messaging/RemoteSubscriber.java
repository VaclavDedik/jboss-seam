package org.jboss.seam.remoting.messaging;

import javax.jms.TopicSubscriber;

/**
 *
 * @author Shane Bryzak
 */
public class RemoteSubscriber
{
  private String token;
  private String topicName;

  private TopicSubscriber subscriber;

  public RemoteSubscriber(String token, String topicName)
  {
    this.token = token;
    this.topicName = topicName;
  }

  public String getToken()
  {
    return token;
  }

  public String getTopicName()
  {
    return topicName;
  }

  public void setTopicSubscriber(TopicSubscriber subscriber)
  {
    this.subscriber = subscriber;
  }

  public TopicSubscriber getTopicSubscriber()
  {
    return subscriber;
  }
}
