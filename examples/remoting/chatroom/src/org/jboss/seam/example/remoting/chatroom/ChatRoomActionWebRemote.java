package org.jboss.seam.example.remoting.chatroom;

import java.util.Set;
import javax.ejb.Local;

import org.jboss.seam.annotations.WebRemote;

@Local
public interface ChatRoomActionWebRemote {
  @WebRemote boolean connect(String name);
  @WebRemote void disconnect();
  @WebRemote void sendMessage(String message);
  @WebRemote Set<String> listUsers();

  public void destroy();
}

