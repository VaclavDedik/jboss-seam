package org.jboss.seam.jbpm;

import javax.faces.context.FacesContext;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SeamActionHandler implements ActionHandler {
   public String expression;

   public void execute(ExecutionContext context) throws Exception {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.getApplication().createMethodBinding(expression, null).invoke(facesContext, null);
   }

}
