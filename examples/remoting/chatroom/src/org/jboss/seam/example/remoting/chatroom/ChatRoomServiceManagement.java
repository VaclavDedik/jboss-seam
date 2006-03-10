package org.jboss.seam.example.remoting.chatroom;

import org.jboss.annotation.ejb.Management;

/**
 * Management interface for ChatRoomService
 */
@Management
public interface ChatRoomServiceManagement
{
  void start() throws Exception;
  void stop();
}
