/**
 * 
 */
package org.jboss.seam.pages;

import javax.faces.context.FacesContext;

import org.jboss.seam.core.Navigator;

public abstract class NavigationHandler extends Navigator
{
   public abstract boolean navigate(FacesContext context);
}