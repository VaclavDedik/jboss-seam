package org.jboss.seam.security.permission;

import java.io.Serializable;
import java.security.Principal;

/**
 * Base class for permissions
 *  
 * @author Shane Bryzak
 */
public class Permission implements Serializable
{
   private Object target;
   private String action;
   private Principal recipient;
   
   public Object getTarget()
   {
      return target;
   }
   
   public void setTarget(Object target)
   {
      this.target = target;
   }
   
   public String getAction()
   {
      return action;
   }
   
   public void setAction(String action)
   {
      this.action = action;
   }
   
   public Principal getRecipient()
   {
      return recipient;
   }
   
   public void setRecipient(Principal recipient)
   {
      this.recipient = recipient;
   }
}
