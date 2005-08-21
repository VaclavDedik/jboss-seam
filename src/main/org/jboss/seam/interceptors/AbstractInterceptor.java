//$Id$
package org.jboss.seam.interceptors;

import org.jboss.seam.Component;

/**
 * Superclass of built-in interceptors
 * 
 * @author Gavin King
 */
class AbstractInterceptor
{
   protected Component component;

   public void setComponent(Component component)
   {
      this.component = component;
   }

}
