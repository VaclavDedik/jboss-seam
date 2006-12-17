/**
 * 
 */
package org.jboss.seam.pages;

public final class Outcome
{
   private NavigationHandler navigationHandler;
   private ConversationControl conversationControl = new ConversationControl();

   public NavigationHandler getNavigationHandler()
   {
      return navigationHandler;
   }

   public void setNavigationHandler(NavigationHandler result)
   {
      this.navigationHandler = result;
   }

   public ConversationControl getConversationControl()
   {
      return conversationControl;
   }
}