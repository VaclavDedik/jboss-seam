package org.jboss.seam.example.remoting.chatroom;

import javax.jms.Topic;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Shane Bryzak
 */
public class Channel
{
  private String channelName;
  private Topic topic;
  private List<String> users = new ArrayList<String>();

  public Channel(String channelName, Topic topic)
  {
    this.topic = topic;
    this.channelName = channelName;
  }

  public String getChannelName()
  {
    return channelName;
  }

  public Topic getTopic()
  {
    return topic;
  }

  public List<String> getUsers()
  {
    return users;
  }
}
