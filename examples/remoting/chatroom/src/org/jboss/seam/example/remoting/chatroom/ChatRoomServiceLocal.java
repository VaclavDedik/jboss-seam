package org.jboss.seam.example.remoting.chatroom;

import javax.ejb.Local;
import java.util.Set;

/**
 *
 */
@Local
public interface ChatRoomServiceLocal
{
  Set<String> getUsers();
  boolean connectUser(String username);
  void disconnectUser(String username);
  boolean isUserConnected(String username);
  String getChatTopicName();
  void publish(ChannelActionDTO action);
}
