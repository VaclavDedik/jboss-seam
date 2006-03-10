package org.jboss.seam.example.remoting.chatroom;

import org.jboss.seam.annotations.Name;
import java.io.Serializable;

/**
 *
 * @author Shane Bryzak
 */
@Name("channelAction")
public class ChannelActionDTO implements Serializable
{
  private String action;
  private String user;
  private String data;

  public ChannelActionDTO(String action, String user, String data)
  {
    this.action = action;
    this.user = user;
    this.data = data;
  }

  public String getAction()
  {
    return action;
  }

  public String getUser()
  {
    return user;
  }

  public String getData()
  {
    return data;
  }
}
