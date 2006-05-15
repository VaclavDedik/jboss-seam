package org.jboss.seam.example.remoting.chatroom;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;

import org.jboss.annotation.ejb.Service;
import org.jboss.logging.Logger;

/**
 *
 */
@Service(objectName = "ejb3:service=ChatRoomService")
public class ChatRoomService implements ChatRoomServiceLocal, ChatRoomServiceManagement
{
  private Logger log = Logger.getLogger(ChatRoomService.class);

  private Topic chatTopic;

  private TopicConnection topicConnection;

  private Set<String> users = new HashSet<String>();

  public void start()
      throws Exception
  {
    InitialContext ctx = new InitialContext();
    TopicConnectionFactory f = (TopicConnectionFactory) ctx.lookup(
        "UIL2ConnectionFactory");

    topicConnection = f.createTopicConnection();
    topicConnection.start();

    TopicSession topicSession = topicConnection.createTopicSession(false,
          javax.jms.Session.AUTO_ACKNOWLEDGE);

    chatTopic = topicSession.createTemporaryTopic();

    topicSession.close();
  }

  public void stop()
  {
    try {
      topicConnection.stop();
    }
    catch (JMSException ex) { }
  }

  public Set<String> getUsers()
  {
    return users;
  }

  public boolean connectUser(String username)
  {
    synchronized(users)
    {
      if (users.contains(username))
        return false;

      users.add(username);
      publish(new ChannelActionDTO("connect", username, null));
      return true;
    }
  }

  public void disconnectUser(String username)
  {
    users.remove(username);
    publish(new ChannelActionDTO("disconnect", username, null));
  }

  public boolean isUserConnected(String username)
  {
    return users.contains(username);
  }

  public synchronized void publish(ChannelActionDTO action)
  {
    TopicSession topicSession = null;
    try
    {
      topicSession = topicConnection.createTopicSession(false,
          javax.jms.Session.AUTO_ACKNOWLEDGE);
      TopicPublisher publisher = topicSession.createPublisher(chatTopic);
      publisher.publish(topicSession.createObjectMessage(action));
      publisher.close();
    }
    catch (Exception ex)
    {
      log.error("Error publishing message", ex);
    }
    finally
    {
      if (topicSession != null)
      {
        try {
          topicSession.close();
        }
        catch (JMSException ex) { }
      }
    }
  }

  public String getChatTopicName()
  {
    try {
      return chatTopic.getTopicName();
    }
    catch (JMSException ex) {
      return null;
    }
  }
}
