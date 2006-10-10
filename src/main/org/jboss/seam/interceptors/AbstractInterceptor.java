//$Id$
package org.jboss.seam.interceptors;

import java.io.Serializable;

import org.jboss.seam.Component;

/**
 * Superclass of built-in interceptors
 * 
 * @author Gavin King
 */
public class AbstractInterceptor implements Serializable
{
   private transient Component component; //a cache of the Component reference
   private String componentName;

   public void setComponent(Component component)
   {
      componentName = component.getName();
      this.component = component;
   }

   protected Component getComponent()
   {
      if (component==null)
      {
         component = Component.forName(componentName);
      }
      return component;
   }

}
