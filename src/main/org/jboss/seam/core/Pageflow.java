package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
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
@Install(dependencies="org.jboss.seam.core.jbpm", precedence=BUILT_IN)
public class Pageflow extends AbstractMutable implements Serializable
{
   private static final LogProvider log = Logging.getLogProvider(Pageflow.class);
   
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
      return (Pageflow) Component.getInstance(Pageflow.class, ScopeType.CONVERSATION);
   }
   
   /**
    * Get the current counter value, used for detecting
    * illegal use of the backbutton.
    */
   public int getPageflowCounter()
   {
      return counter;
   }
   
   /**
    * Check that the current state of the pageflow matches
    * what is expected by the faces request.
    */
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

   /**
    * Add a message to indicate that illegal navigation
    * occurred. May be overridden by user to perform
    * special processing.
    */
   protected void illegalNavigation()
   {
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.IllegalNavigation", 
            "Illegal navigation" 
         );
   }
   
   /**
    * Get the current Node of the pageflow.
    */
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
   
   /**
    * Reposition the pageflow at the named node.
    * 
    * @param nodeName the name of a node
    */
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
   
   /**
    * Get the current Page of the pageflow.
    */
   public Page getPage() 
   {
      Node node = getNode();
      if ( !(node instanceof Page) )
      {
         throw new IllegalStateException("pageflow is not currently at a <page> or <start-page> node (note that pageflows that begin during the RENDER_RESPONSE phase should use <start-page> instead of <start-state>)");
      }
      return (Page) node;
   }
   
   /**
    * Navigate to the current page.
    */
   protected void navigate(FacesContext context) 
   {
      Page page = getPage();
      if ( !page.isRedirect() )
      {
         render(context, page);
      }
      else
      {
         redirect(page);
      }

      counter++;
      setDirty();
   }

   /**
    * Redirect to the Page.
    */
   protected void redirect(Page page)
   {
      Manager.instance().redirect( getViewId(page) );
   }

   /**
    * Proceed to render the Page.
    */
   protected void render(FacesContext context, Page page)
   {
      UIViewRoot viewRoot = context.getApplication().getViewHandler()
            .createView( context, getViewId(page) );
      context.setViewRoot(viewRoot);
   }

   /**
    * Allows the user to extend this class and use some
    * logical naming of pages other than the JSF view id
    * in their pageflow.
    * 
    * @param page the Page object
    * @return a JSF view id
    */
   protected String getViewId(Page page)
   {
      return page.getViewId();
   }
   
   /**
    * Get the JSF view id of the current page in the
    * pageflow.
    */
   public String getPageViewId()
   {
      return getViewId( getPage() );
   }

   /**
    * Does the current node have a default transition?
    */
   public boolean hasDefaultTransition()
   {
      //we don't use jBPM's default transition,
      //instead we use the "anonymous" transition
      return getNode().getLeavingTransition(null)!=null;
   }
   
   private boolean isNullOutcome(String outcome) 
   {
      return outcome==null || "".equals(outcome);
   }

   public boolean hasTransition(String outcome)
   {
      return isNullOutcome(outcome) ? 
            hasDefaultTransition() : 
            getNode().getLeavingTransition(outcome)!=null;
   }

   /**
    * Given the JSF action outcome, perform navigation according
    * to the current pageflow.
    */
   public void navigate(FacesContext context, String outcome) {
      if ( isNullOutcome(outcome) )
      {
         //if it has a default transition defined, trigger it,
         //otherwise just redisplay the page
         if ( hasDefaultTransition() )
         {
            //we don't use jBPM's default transition,
            //instead we use the "anonymous" transition
            PageflowHelper.signal(processInstance, null);
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

   /**
    * Process events defined in the pageflow.
    * 
    * @param type one of: "process-validations", "update-model-values",
    *                     "invoke-application", "render-response"
    */
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
   
   /**
    * Begin executing a pageflow.
    * 
    * @param pageflowDefinitionName the name of the pageflow definition
    */
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
         return ( (Page) node ).getNoConversationViewId();
      }
      else
      {
         return null;
      }
   }

   protected ProcessDefinition getPageflowProcessDefinition(String pageflowName)
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
