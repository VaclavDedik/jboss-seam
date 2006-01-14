//$Id$
package org.jboss.seam.example.messages;

import javax.ejb.Local;

@Local
public interface MessageList
{
   public void findMessages();
   public String select();
   public String delete();
   public void destroy();
}