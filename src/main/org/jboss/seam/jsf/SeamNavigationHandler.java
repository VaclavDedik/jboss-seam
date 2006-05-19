package org.jboss.seam.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;

public class SeamNavigationHandler extends NavigationHandler {
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) {
      //if ( !"org.jboss.seam.switch".equals(outcome) ) 
      if ( !context.getResponseComplete() ) //workaround for a bug in MyFaces
      {
         boolean outcomeIsViewId = outcome!=null && outcome.startsWith("/");
         if ( outcomeIsViewId )
         {
            Manager.instance().redirect(outcome);
         }
         else if ( Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() && Pageflow.instance().hasTransition(outcome) )
         {
            Pageflow.instance().navigate(context, outcome);
         }
         else
         {
            baseNavigationHandler.handleNavigation(context, fromAction, outcome);
         }
      }
   }

}
