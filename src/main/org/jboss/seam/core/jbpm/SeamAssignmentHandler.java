package org.jboss.seam.core.jbpm;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.jboss.seam.util.Strings;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;

public class SeamAssignmentHandler implements AssignmentHandler {
   public String pooledActors;
   public String actorId;

   public void assign(Assignable assignable, ExecutionContext context) throws Exception {
      if (pooledActors!=null)
      {
         String[] result;
         if ( pooledActors.startsWith("#") )
         {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            Application application = facesCtx.getApplication();
            Object object = application.createValueBinding(pooledActors).getValue(facesCtx);
            result = (object instanceof String) ?
                  new String[] { (String) object } :
                  (String[]) object;
         }
         else
         {
            result = Strings.split(pooledActors, ", ");
         }
         assignable.setPooledActors(result);
      }
      else if (actorId!=null)
      {
         String result;
         if ( actorId.startsWith("#") )
         {
            FacesContext facesCtx = FacesContext.getCurrentInstance();
            Application application = facesCtx.getApplication();
            result = (String) application.createValueBinding(actorId).getValue(facesCtx);
         }
         else
         {
            result = actorId;
         }
         assignable.setActorId(result);
      }
      else
      {
         throw new IllegalArgumentException();
      }
   }

}
