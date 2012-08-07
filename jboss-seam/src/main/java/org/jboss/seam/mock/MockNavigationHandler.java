package org.jboss.seam.mock;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.context.FacesContext;

public class MockNavigationHandler extends ConfigurableNavigationHandler
{

   @Override
   public void handleNavigation(FacesContext context, String action, String outcome)
   {

   }

   @Override
   public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome)
   {
      return null;
   }

   @Override
   public Map<String, Set<NavigationCase>> getNavigationCases()
   {
      return null;
   }

}
