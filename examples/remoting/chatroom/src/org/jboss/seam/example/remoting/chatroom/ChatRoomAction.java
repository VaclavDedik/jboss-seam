package org.jboss.seam.example.remoting.chatroom;

import javax.annotation.EJB;
import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import static org.jboss.seam.ScopeType.CONVERSATION;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;
import java.util.Set;

@Stateful
@Name("chatroomAction")
@Scope(CONVERSATION)
@Interceptors(SeamInterceptor.class)
public class ChatRoomAction implements ChatRoomLocal {

  @EJB ChatRoomServiceLocal chatRoomService;

  @In(required = false) @Out(scope = CONVERSATION) String username;

  @Begin
  public boolean connect(String username)
  {
    this.username = username;
    return chatRoomService.connectUser(username);
  }

  public void sendMessage(String message)
  {
    chatRoomService.publish(new ChannelActionDTO("message", username, message));
  }

  @End
  public void disconnect()
  {
    chatRoomService.disconnectUser(username);
  }

  public String getChatTopicName()
  {
    return chatRoomService.getChatTopicName();
  }

  public Set<String> listUsers()
  {
    return chatRoomService.getUsers();
  }
  
  @Destroy @Remove 
  public void destroy() {}
  
}
