package org.jboss.seam.navigation;

import javax.faces.context.FacesContext;

import org.jboss.seam.faces.Navigator;

public abstract class NavigationHandler extends Navigator
{
   public abstract boolean navigate(FacesContext context);
}