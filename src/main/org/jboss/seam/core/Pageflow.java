package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

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
public class Pageflow 
{
   
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
   
   public void validatePageflow(Map attributes) 
   {
      if ( processInstance!=null )
      {
         Integer counter = (Integer) attributes.get(Manager.PAGEFLOW_COUNTER);
         if ( counter!=null && getPageflowCounter()!=counter )
         {
            FacesContext context = FacesContext.getCurrentInstance();
            navigate(context);
            context.renderResponse();
         }
      }
   }
   
   public Page getPage() 
   {
      if (processInstance==null) return null;
      Token pageFlowToken = processInstance.getRootToken();
      Page page = (Page) pageFlowToken.getNode();
      return page;
   }
   
   private void navigate(FacesContext context) 
   {
      Page page = getPage();
      if ( !page.isRedirect() )
      {
         UIViewRoot viewRoot = context.getApplication().getViewHandler().createView( context, page.getViewId() );
         context.setViewRoot(viewRoot);
      }
      else
      {
         Manager.instance().redirect( page.getViewId() );
      }

      counter++;
   }

   public boolean hasDefaultTransition()
   {
      return getPage().getDefaultLeavingTransition()!=null;
   }
   
   private boolean isNullOutcome(String outcome) {
      return outcome==null || "".equals(outcome);
   }

   public boolean hasTransition(String outcome)
   {
      return isNullOutcome(outcome) ? 
            hasDefaultTransition() : 
            getPage().getLeavingTransition(outcome)!=null;
   }

   public void navigate(FacesContext context, String outcome) {
      if ( isNullOutcome(outcome) )
      {
         //if it has a default transition defined, trigger it,
         //otherwise just redisplay the page
         if ( hasDefaultTransition() )
         {
            processInstance.signal();
            navigate(context);
         }
      }
      else
      {
         //trigger the named transition
         processInstance.signal(outcome);
         navigate(context);
      }
   }
   
}
