package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.PerNestedConversation;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.pageflow.Page;
import org.jboss.seam.pageflow.PageflowHelper;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

/**
 * A Seam component that manages the current
 * jBPM ProcessInstance used for pageflow.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@PerNestedConversation
@Name("org.jboss.seam.core.pageflow")
@Intercept(NEVER)
public class Pageflow extends AbstractMutable implements Serializable
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
      setDirty();
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
         org.jboss.seam.core.FacesPage page = org.jboss.seam.core.FacesPage.instance();
         String pageflowName = page.getPageflowName();
         String pageflowNodeName = page.getPageflowNodeName();
         boolean canReposition = getPage().isBackEnabled() && 
               processInstance.getProcessDefinition().getName().equals(pageflowName) && //probably not necessary
               pageflowNodeName!=null; //probably not necessary
         if (canReposition)
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
            Integer pageCounter = org.jboss.seam.core.FacesPage.instance().getPageflowCounter();
            if ( pageCounter!=null && getPageflowCounter()!=pageCounter )
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
      illegalNavigation();
      context.renderResponse();
   }

   protected void illegalNavigation()
   {
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.IllegalNavigation", 
            "Illegal navigation" 
         );
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
      if (processInstance==null)
      {
         throw new IllegalStateException("no pageflow in progress");
      }
      Node node = processInstance.getProcessDefinition().getNode(nodeName);
      if (node==null)
      {
         throw new IllegalArgumentException(
               "no node named: " + nodeName + 
               " for pageflow: " + processInstance.getProcessDefinition().getName()
            );
      }
      processInstance.getRootToken().setNode(node);
      setDirty();
   }
   
   public Page getPage() 
   {
      Node node = getNode();
      if ( !(node instanceof Page) )
      {
         throw new IllegalStateException("pageflow is not currently at a <page> or <start-page> node (note that pageflows that begin during the RENDER_RESPONSE phase should use <start-page> instead of <start-state>)");
      }
      return (Page) node;
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
      setDirty();
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
      
      if ( processInstance.hasEnded() )
      {
         Events.instance().raiseEvent("org.jboss.seam.endPageflow." + processInstance.getProcessDefinition().getName());
      }
   }

   public void processEvents(String type)
   {
      Event event = getNode().getEvent(type);
      if (event!=null)
      {
         for ( Action action: (List<Action>) event.getActions() )
         {
            try
            {
               action.execute( ExecutionContext.currentExecutionContext() );
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }
   
   public void begin(String pageflowDefinitionName)
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("beginning pageflow: " + pageflowDefinitionName);
      }
      
      processInstance = PageflowHelper.newPageflowInstance( getPageflowProcessDefinition(pageflowDefinitionName) );
      
      //if ( Lifecycle.getPhaseId().equals(PhaseId.RENDER_RESPONSE) ) 
      //{
    	  //if a pageflow starts during the render response phase
    	  //(as a result of a @Create method), we know the navigation
    	  //handler will not get called, so we should force the
    	  //pageflow out of the start state immediately
        //TODO: this is not actually completely true, what about <s:actionLink/>
    	  //pi.signal();
      //}
      
      setDirty();
      
      Events.instance().raiseEvent("org.jboss.seam.beginPageflow." + pageflowDefinitionName);
      
      storePageflowToViewRootIfNecessary();

   }

   private void storePageflowToViewRootIfNecessary()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( facesContext!=null && Lifecycle.getPhaseId()==PhaseId.RENDER_RESPONSE )
      {
         FacesPage.instance().storePageflow();
      }
   }
   
   public String getNoConversationViewId(String pageflowName, String pageflowNodeName)
   {
      ProcessDefinition pageflowProcessDefinition = getPageflowProcessDefinition(pageflowName);
      Node node = pageflowProcessDefinition.getNode(pageflowNodeName);
      if (node!=null && node instanceof Page)
      {
         Page page = (Page) node;
         return page.getNoConversationViewId();
      }
      else
      {
         return null;
      }
   }

   private ProcessDefinition getPageflowProcessDefinition(String pageflowName)
   {
      ProcessDefinition pageflowProcessDefinition = Jbpm.instance().getPageflowProcessDefinition(pageflowName);
      if (pageflowProcessDefinition==null)
      {
         throw new IllegalArgumentException("pageflow definition not found: " + pageflowName);
      }
      return pageflowProcessDefinition;
   }
   
   @Override
   public String toString()
   {
      String name = processInstance==null ? 
            "null" : processInstance.getProcessDefinition().getName();
      return "Pageflow(" + name + ")";
   }

}
