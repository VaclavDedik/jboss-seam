package org.jboss.seam.ioc.spring;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.springframework.aop.TargetSource;

/**
 * A TargetSource for a seam component instance. Will obtain an instance given a name and optionally a scope and create.
 * Used by the SeamFactoryBean to create a proxy for a requested seam component instance.
 *
 * @author youngm
 */
@SuppressWarnings("serial")
public class SeamTargetSource implements TargetSource, Serializable 
{

	private ScopeType scope;

	private String name;

	private Boolean create;

	/**
	 * @param name Name of the component: required
	 * @param scope Name of the scope the component is in: optional
	 * @param create Whether to create a new instance if one doesn't already exist: optional
	 */
	public SeamTargetSource(String name, ScopeType scope, Boolean create) 
   {
		if (name == null || "".equals(name)) 
      {
			throw new IllegalArgumentException("Name is required.");
		}
		this.name = name;
		this.scope = scope;
		this.create = create;
	}

	/**
	 * Returns a component instance for this TargetSource.
	 *
	 * @see org.springframework.aop.TargetSource#getTarget()
	 */
	public Object getTarget() throws Exception 
   {
		if (scope == null && create == null) 
      {
			return Component.getInstance(name);
		} 
      else if (scope == null) 
      {
			return Component.getInstance(name, create);
		} 
      else if (create == null) 
      {
			return Component.getInstance(name, scope);
		} 
      else 
      {
			return Component.getInstance(name, scope, create);
		}
	}

	/**
	 * Obtains the seam component beanClass for this TargetSource.
	 *
	 * @see org.springframework.aop.TargetSource#getTargetClass()
	 */
	public Class getTargetClass() 
   {
		return getComponent().getBeanClass();
	}

	/**
	 * Get the component for this TargetSource
	 *
	 * @return component
	 */
	public Component getComponent() 
   {
		// TODO reuse
		boolean unmockApplication = false;
		if (!Contexts.isApplicationContextActive()) 
      {
			Lifecycle.mockApplication();
			unmockApplication = true;
		}
		try {
			Component component = Component.forName(name);
			if (component == null) 
         {
				throw new IllegalStateException("Cannot find targetClass for seam component: " + name
						+ ".  Make sure Seam is being configured before Spring.");
			}
			return component;
		} 
      finally 
      {
			if (unmockApplication) 
         {
				Lifecycle.unmockApplication();
			}
		}
	}

	/**
	 * @see org.springframework.aop.TargetSource#isStatic()
	 */
	public boolean isStatic() 
   {
		return false;
	}

	/**
	 * Don't think we need to do anything here.
	 *
	 * @see org.springframework.aop.TargetSource#releaseTarget(java.lang.Object)
	 */
	public void releaseTarget(Object target) throws Exception 
   {
		// Do Nothing
	}
}
