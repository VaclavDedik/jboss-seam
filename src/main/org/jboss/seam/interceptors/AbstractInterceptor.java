//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;

import org.jboss.seam.Component;

/**
 * Superclass of built-in interceptors
 * 
 * @author Gavin King
 */
class AbstractInterceptor
{
   protected transient Component component;
   private String componentName;

   public void setComponent(Component component)
   {
      this.component = component;
   }
   
   @PrePassivate
   public void initComponentName()
   {
      componentName = component.getName();
   }
   
   @PostActivate
   public void initComponent()
   {
      component = Component.forName(componentName);
   }

}
