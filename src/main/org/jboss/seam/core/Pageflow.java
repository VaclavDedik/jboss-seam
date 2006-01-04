package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.jbpm.Page;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

/**
 * A Seam component that manages the current
 * jBPM ProcessInstance used for pageflow.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Name("org.jboss.seam.core.pageflow")
@Intercept(NEVER)
public class Pageflow {
   
   private int counter;
   
   private ProcessInstance processInstance;
   
   public boolean isInProcess()
   {
      return processInstance!=null;
   }

   public ProcessInstance getProcessInstance() {
      return processInstance;
   }

   public void setProcessInstance(ProcessInstance processInstance) {
      this.processInstance = processInstance;
   }
   
   public static Pageflow instance()
   {
      if ( !Contexts.isConversationContextActive() )
      {
         throw new IllegalStateException("No active conversation context");
      }
      return (Pageflow) Component.getInstance(Pageflow.class, ScopeType.CONVERSATION, true);
   }
   
   public int getPageflowCounter()
   {
      return counter;
   }
   
   public void validatePageflow(Map attributes) {
      Integer counter = (Integer) attributes.get(Manager.PAGEFLOW_COUNTER);
      if ( counter!=null && getPageflowCounter()!=counter )
      {
         FacesContext context = FacesContext.getCurrentInstance();
         navigate(context);
         context.renderResponse();
      }
   }
   
   public Page getPage(ProcessInstance processInstance) {
      Token pageFlowToken = processInstance.getRootToken();
      Page page = (Page) pageFlowToken.getNode();
      return page;
   }
   
   public void navigate(FacesContext context) {
      Page page = getPage(processInstance);
      if ( !page.isRedirect() )
      {
         UIViewRoot viewRoot = context.getApplication().getViewHandler().createView( context, page.getViewId() );
         context.setViewRoot(viewRoot);
      }
      else
      {
         Manager manager = Manager.instance();
         manager.beforeRedirect();
         String url = context.getApplication().getViewHandler().getActionURL( context, page.getViewId() );
         if ( manager.isLongRunningConversation() )
         {
            url += "?conversationId=" + manager.getCurrentConversationId();
         }
         try
         {
            context.getExternalContext().redirect(url);
         }
         catch (IOException ioe)
         {
            throw new RuntimeException("could not redirect to: " + url, ioe);
         }
         context.responseComplete(); //work around MyFaces bug in 1.1.1
      }

      counter++;
   }

   public boolean hasDefaultTransition()
   {
      return getPage(processInstance).getDefaultLeavingTransition()!=null;
   }
   
}
