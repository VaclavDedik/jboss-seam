package org.jboss.seam.example.remoting.chatroom;

import java.util.Set;
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

@Stateful
@Name("chatroomAction")
@Scope(CONVERSATION)
public class ChatRoomAction implements ChatRoomLocal {

  @In(create = true) ChatRoomService chatRoomService;

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

  public Set<String> listUsers()
  {
    return chatRoomService.getUsers();
  }

  @Destroy @Remove
  public void destroy() {}

}
