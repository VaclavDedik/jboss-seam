/**
 * 
 */
package org.jboss.seam.exceptions;


public final class ConfigErrorHandler extends ErrorHandler
{
   private final String message;
   private final boolean conversation;
   private final Class clazz;
   private final int code;
   private final boolean rollback;

   public ConfigErrorHandler(String message, boolean conversation, Class clazz, int code, boolean rollback)
   {
      this.message = message;
      this.conversation = conversation;
      this.clazz = clazz;
      this.code = code;
      this.rollback = rollback;
   }

   @Override
   protected String getMessage(Exception e)
   {
      return message;
   }

   @Override
   protected int getCode(Exception e)
   {
      return code;
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

   @Override
   protected boolean isRollback(Exception e)
   {
      return rollback;
   }
}