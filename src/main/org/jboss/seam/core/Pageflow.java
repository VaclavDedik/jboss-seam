package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Mutable;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.pageflow.Page;
import org.jboss.seam.pageflow.PageflowHelper;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
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
@Mutable
public class Pageflow implements Serializable
{
   
   private static final Log log = LogFactory.getLog(Pageflow.class);
   
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
   
   public void validatePageflow() 
   {
      if ( processInstance!=null )
      {
         
         String pageflowName = (String) Contexts.getPageContext().get(Manager.PAGEFLOW_NAME);
         String pageflowNodeName = (String) Contexts.getPageContext().get(Manager.PAGEFLOW_NODE_NAME);
         boolean canReposition = getPage().isBackEnabled() && 
               processInstance.getProcessDefinition().getName().equals(pageflowName) && //probably not necessary
               pageflowNodeName!=null; //probably not necessary
         if ( canReposition )
         {
            //check the node name to make sure we are still on the same node
            if ( !pageflowNodeName.equals( getNode().getName() ) )
            {
               //legal use of back/forward button, so reposition
               reposition(pageflowNodeName);
            }
         }
         else
         {
            //check the counter to detect illegal use of backbutton
            //Integer counter = (Integer) attributes.get(Manager.PAGEFLOW_COUNTER);
            Integer counter = (Integer) Contexts.getPageContext().get(Manager.PAGEFLOW_COUNTER);
            if ( counter!=null && getPageflowCounter()!=counter )
            {
               illegalNavigationError();
            }
         }
         
      }
   }

   private void illegalNavigationError()
   {
      FacesContext context = FacesContext.getCurrentInstance();
      navigate(context);
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.IllegalNavigation", 
            "Illegal navigation" 
         );
      context.renderResponse();
   }
   
   public Node getNode() 
   {
      if (processInstance==null) return null;
      Token pageFlowToken = processInstance.getRootToken();
      Node node = pageFlowToken.getNode();
      if (node==null) 
      {
         throw new IllegalStateException("pageflow has not yet started");
      }
      return node;
   }
   
   public void reposition(String nodeName)
   {
      Node node = processInstance.getProcessDefinition().getNode(nodeName);
      processInstance.getRootToken().setNode(node);
   }
   
   public Page getPage() 
   {
      return (Page) getNode();
   }
   
   private void navigate(FacesContext context) 
   {
      Page page = getPage();
      if ( !page.isRedirect() )
      {
         UIViewRoot viewRoot = context.getApplication().getViewHandler()
               .createView( context, page.getViewId() );
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
      //we don't use jBPM's default transition,
      //instead we use the "anonymous" transition
      return getNode().getLeavingTransition(null)!=null;
   }
   
   private boolean isNullOutcome(String outcome) {
      return outcome==null || "".equals(outcome);
   }

   public boolean hasTransition(String outcome)
   {
      return isNullOutcome(outcome) ? 
            hasDefaultTransition() : 
            getNode().getLeavingTransition(outcome)!=null;
   }

   public void navigate(FacesContext context, String outcome) {
      if ( isNullOutcome(outcome) )
      {
         //if it has a default transition defined, trigger it,
         //otherwise just redisplay the page
         if ( hasDefaultTransition() )
         {
            //we don't use jBPM's default transition,
            //instead we use the "anonymous" transition
            PageflowHelper.signal(processInstance, (String) null);
            navigate(context);
         }
      }
      else
      {
         //trigger the named transition
         PageflowHelper.signal(processInstance, outcome);
         navigate(context);
      }
   }
   
   public void begin(String pageflowDefinitionName)
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("beginning pageflow: " + pageflowDefinitionName);
      }
      
      ProcessDefinition pd = Jbpm.instance().getPageflowProcessDefinition(pageflowDefinitionName);
      if (pd==null)
      {
         throw new IllegalArgumentException("pageflow definition not found: " + pageflowDefinitionName);
      }
      ProcessInstance pi = PageflowHelper.newPageflowInstance(pd);
      setProcessInstance(pi);
      //if ( Lifecycle.getPhaseId().equals(PhaseId.RENDER_RESPONSE) ) 
      //{
    	  //if a pageflow starts during the render response phase
    	  //(as a result of a @Create method), we know the navigation
    	  //handler will not get called, so we should force the
    	  //pageflow out of the start state immediately
        //TODO: this is not actually completely true, what about <s:actionLink/>
    	  //pi.signal();
      //}
   }
   
   public String toString()
   {
      String name = processInstance==null ? 
            "null" : processInstance.getProcessDefinition().getName();
      return "Pageflow(" + name + ")";
   }
   
}
