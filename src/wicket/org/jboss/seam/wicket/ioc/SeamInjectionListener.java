package org.jboss.seam.wicket.ioc;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;

/**
 * Repsonsible for injecting dynamic proxies into Wicket classes
 *
 * @author Pete Muir
 */
public class SeamInjectionListener implements IComponentInstantiationListener 
{

	public void onInstantiation(Component component) 
	{
	   WicketComponent wicketComponent = WicketComponent.forClass(component.getClass());
	   try
	   {
	      wicketComponent.inject(component);
	   }
	   catch (Exception e) 
	   {
         throw new RuntimeException(e);
      }
	}
}
