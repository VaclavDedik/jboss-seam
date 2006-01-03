package org.jboss.seam.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Init;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.jbpm.Page;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

public class SeamNavigationHandler extends NavigationHandler {
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) {
      if ( Init.instance().isJbpmInstalled() )
      {
         ProcessInstance processInstance = Pageflow.instance().getProcessInstance();
         if (processInstance==null)
         {
            baseNavigationHandler.handleNavigation(context, fromAction, outcome);
         }
         else
         {
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
      else
      {
         baseNavigationHandler.handleNavigation(context, fromAction, outcome);
      }
   }

   private void navigate(FacesContext context, ProcessInstance processInstance) {
      Page page = getPage(processInstance);
      UIViewRoot viewRoot = context.getApplication().getViewHandler().createView( context, page.getViewId() );
      context.setViewRoot(viewRoot);
      //context.getViewRoot().setViewId( page.getUrl() );
   }

   private Page getPage(ProcessInstance processInstance) {
      Token pageFlowToken = processInstance.getRootToken();
      Page page = (Page) pageFlowToken.getNode();
      return page;
   }

}
