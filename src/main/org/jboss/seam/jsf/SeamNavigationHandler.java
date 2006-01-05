package org.jboss.seam.jsf;


import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Init;
import org.jboss.seam.core.Pageflow;
import org.jbpm.graph.exe.ProcessInstance;

public class SeamNavigationHandler extends NavigationHandler {
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) {
	  if ( !"org.jboss.seam.switch".equals(outcome) ) //TODO: is if ( !context.getResponseComplete() ) better?
	  {
	      if ( Init.instance().isJbpmInstalled() )
	      {
	         Pageflow pageflow = Pageflow.instance();
	         ProcessInstance processInstance = pageflow.getProcessInstance();
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
	               if ( pageflow.hasDefaultTransition() )
	               {
	                  processInstance.signal();
	                  pageflow.navigate(context);
	               }
	            }
	            else
	            {
	               //trigger the named transition
	               processInstance.signal(outcome);
	               pageflow.navigate(context);
	            }
	         }
	      }
	      else
	      {
	         baseNavigationHandler.handleNavigation(context, fromAction, outcome);
	      }
	  }
   }

}
