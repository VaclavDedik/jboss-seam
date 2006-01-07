package org.jboss.seam.jbpm;

import javax.faces.context.FacesContext;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

public class SeamDecisionHandler implements DecisionHandler {
   public String expression;

   public String decide(ExecutionContext context) throws Exception {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return facesContext.getApplication().createValueBinding(expression)
            .getValue(facesContext).toString();
   }

}
