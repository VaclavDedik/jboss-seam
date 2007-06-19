package org.jboss.seam.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Init;
import org.jboss.seam.faces.JsfManager;
import org.jboss.seam.navigation.Pageflow;
import org.jboss.seam.navigation.Pages;

public class SeamNavigationHandler extends NavigationHandler {
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) {
      if ( !context.getResponseComplete() ) //workaround for a bug in MyFaces
      {
         if ( isOutcomeViewId(outcome) )
         {
            JsfManager.instance().interpolateAndRedirect(outcome);
         }
         else if ( Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() && Pageflow.instance().hasTransition(outcome) )
         {
            Pageflow.instance().navigate(context, outcome);
         }
         else if ( !Pages.instance().navigate(context, fromAction, outcome) )
         {
            baseNavigationHandler.handleNavigation(context, fromAction, outcome);
         }
      }
   }

   private static boolean isOutcomeViewId(String outcome)
   {
      return outcome!=null && outcome.startsWith("/");
   }

}
