package org.jboss.seam.jbpm;

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
            Object object = getValue(pooledActors);
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
            result = (String) getValue(actorId);
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

   private Object getValue(String expression) {
      FacesContext facesCtx = FacesContext.getCurrentInstance();
      return facesCtx.getApplication()
            .createValueBinding(expression)
            .getValue(facesCtx);
   }

}
