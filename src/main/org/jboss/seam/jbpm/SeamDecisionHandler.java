package org.jboss.seam.jbpm;

import javax.faces.context.FacesContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class SeamDecisionHandler implements DecisionHandler {
   public String action;

   public String decide(ExecutionContext context) throws Exception {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return (String) facesContext.getApplication().createMethodBinding(action, null).invoke(facesContext, null);
   }

}
