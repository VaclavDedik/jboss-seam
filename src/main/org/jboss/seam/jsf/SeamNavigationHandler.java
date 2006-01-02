package org.jboss.seam.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Conversation;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.Page;

public class SeamNavigationHandler extends NavigationHandler {
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) {
      ProcessInstance processInstance = Conversation.instance().getProcessInstance();
      //Long processId = Conversation.instance().getProcessId();
      //if (processId==null)
      if (processInstance==null)
      {
         baseNavigationHandler.handleNavigation(context, fromAction, outcome);
      }
      else
      {
         //JbpmSession session = ManagedJbpmSession.instance();
         //ProcessInstance processInstance = session.getGraphSession().loadProcessInstance(processId);
         if ( outcome==null || "".equals(outcome) )
         {
            //if it has a default transition defined, trigger it,
            //otherwise just redisplay the page
            boolean hasDefaultTransition = getPage(processInstance).getDefaultLeavingTransition()!=null;
            if ( hasDefaultTransition )
            {
               processInstance.signal();
               navigate(context, processInstance);
            }
         }
         else
         {
            //trigger the named transition
            processInstance.signal(outcome);
            navigate(context, processInstance);
         }
      }
   }

   private void navigate(FacesContext context, ProcessInstance processInstance) {
      Page page = getPage(processInstance);
      UIViewRoot viewRoot = context.getApplication().getViewHandler().createView( context, page.getUrl() );
      context.setViewRoot(viewRoot);
      //context.getViewRoot().setViewId( page.getUrl() );
   }

   private Page getPage(ProcessInstance processInstance) {
      Token pageFlowToken = processInstance.getRootToken();
      Page page = (Page) pageFlowToken.getNode();
      return page;
   }

}
