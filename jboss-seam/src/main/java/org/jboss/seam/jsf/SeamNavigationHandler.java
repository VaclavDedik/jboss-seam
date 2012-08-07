package org.jboss.seam.jsf;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Init;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.pageflow.Pageflow;

/**
 * Adds three new navigation possibilities beyond the
 * frumpy JSF navigation rules: returning the view id
 * directly, pages.xml, and jPDL-based pageflows.
 * 
 * @author Gavin King
 *
 */
public class SeamNavigationHandler extends ConfigurableNavigationHandler 
{
   
   private final NavigationHandler baseNavigationHandler;
   
   public SeamNavigationHandler(NavigationHandler navigationHandler)
   {
      this.baseNavigationHandler = navigationHandler;
   }

   @Override
   public void handleNavigation(FacesContext context, String fromAction, String outcome) 
   {
      if ( !context.getResponseComplete() ) //workaround for a bug in MyFaces
      {
         if ( isOutcomeViewId(outcome) )
         {
            FacesManager.instance().interpolateAndRedirect(outcome);
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

   @Override
   public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome)
   {
      if (baseNavigationHandler instanceof ConfigurableNavigationHandler)
      {
         return ((ConfigurableNavigationHandler) baseNavigationHandler).getNavigationCase(context, fromAction, outcome);
      }
      else
      {
         return null;
      }
   }

   @Override
   public Map<String, Set<NavigationCase>> getNavigationCases()
   {
      if (baseNavigationHandler instanceof ConfigurableNavigationHandler)
      {
         return ((ConfigurableNavigationHandler) baseNavigationHandler).getNavigationCases();
      }
      else
      {
         return null;
      }
   }

}
