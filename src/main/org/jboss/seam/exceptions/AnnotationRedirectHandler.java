/**
 * 
 */
package org.jboss.seam.exceptions;

import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Redirect;
import org.jboss.seam.contexts.Lifecycle;

public class AnnotationRedirectHandler extends RedirectHandler
{
   @Override
   public boolean isHandler(Exception e)
   {
      return e.getClass().isAnnotationPresent(Redirect.class) && 
            Lifecycle.getPhaseId()!=PhaseId.RENDER_RESPONSE && 
            Lifecycle.getPhaseId()!=null;
   }
   
   @Override
   protected String getMessage(Exception e)
   {
      return e.getClass().getAnnotation(Redirect.class).message();
   }
   
   @Override
   protected String getViewId(Exception e)
   {
      return e.getClass().getAnnotation(Redirect.class).viewId();
   }
   
   @Override
   protected boolean isEnd(Exception e)
   {
      return e.getClass().getAnnotation(Redirect.class).end();
   } 

   @Override
   protected boolean isRollback(Exception e)
   {
      return e.getClass().getAnnotation(Redirect.class).rollback();
   }
   
   @Override
   public String toString()
   {
      return "RedirectHandler";
   }
}