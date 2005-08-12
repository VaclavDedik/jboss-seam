/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

/**
 * A conversation context is a context that last longer than a request
 * but shorter than an application
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class ConversationContext implements Context
{

   private Context loginContext;

   private String conversationId;

   public ConversationContext(Context loginContext, String conversationId)
   {
      this.loginContext = loginContext;
      this.conversationId = conversationId;
   }

   public Object get(String name)
   {
      return loginContext.get(name + '$' + conversationId);
   }

   public void set(String name, Object value)
   {
      loginContext.set(name + '$' + conversationId, value);
   }

   public boolean isSet(String name)
   {
      return get(name) != null;
   }

}
