/**
 * 
 */
package org.jboss.seam.exceptions;

import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Render;
import org.jboss.seam.contexts.Lifecycle;

public class AnnotationRenderHandler extends RenderHandler
{
   @Override
   public boolean isHandler(Exception e)
   {
      return e.getClass().isAnnotationPresent(Render.class) && 
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION;
   }

   @Override
   protected String getMessage(Exception e)
   {
      return e.getClass().getAnnotation(Render.class).message();
   }
   
   @Override
   protected String getViewId(Exception e)
   {
      return e.getClass().getAnnotation(Render.class).viewId();
   }

   @Override
   protected boolean isEnd(Exception e)
   {
      return e.getClass().getAnnotation(Render.class).end();
   }

   @Override
   protected boolean isRollback(Exception e)
   {
      return e.getClass().getAnnotation(Render.class).rollback();
   }
   
   @Override
   public String toString()
   {
      return "RenderHandler";
   }
}