/**
 * 
 */
package org.jboss.seam.exceptions;

import javax.faces.event.PhaseId;

import org.jboss.seam.contexts.Lifecycle;

public final class ConfigRenderHandler extends RenderHandler
{
   private final String message;
   private final String id;
   private final Class clazz;
   private final boolean rollback;
   private final boolean conversation;

   public ConfigRenderHandler(String message, String id, Class clazz, boolean rollback, boolean conversation)
   {
      this.message = message;
      this.id = id;
      this.clazz = clazz;
      this.rollback = rollback;
      this.conversation = conversation;
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
      return clazz.isInstance(e) && 
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION;
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