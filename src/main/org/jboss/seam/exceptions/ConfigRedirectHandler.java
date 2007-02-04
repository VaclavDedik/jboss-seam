/**
 * 
 */
package org.jboss.seam.exceptions;


public final class ConfigRedirectHandler extends RedirectHandler
{
   private final String id;
   private final Class clazz;
   private final boolean conversation;
   private final boolean rollback;
   private final String message;

   public ConfigRedirectHandler(String id, Class clazz, boolean conversation, boolean rollback, String message)
   {
      this.id = id;
      this.clazz = clazz;
      this.conversation = conversation;
      this.rollback = rollback;
      this.message = message;
   }

   @Override
   protected String getMessage(Exception e)
   {
      return message;
   }

   @Override
   protected String getViewId(Exception e)
   {
      return id;
   }

   @Override
   public boolean isHandler(Exception e)
   {
      return clazz.isInstance(e);
   }

   @Override
   protected boolean isEnd(Exception e)
   {
      return conversation;
   }

   protected boolean isRollback(Exception e)
   {
      return rollback;
   }
}