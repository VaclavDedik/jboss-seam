package org.jboss.seam.ioc.spring;

import org.jboss.seam.ScopeType;
import org.jboss.seam.ioc.IoCComponent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;

/**
 * An extension of Component that allows spring to provide the base instance for
 * a seam component.
 *
 * @author youngm
 */
public class SpringComponent extends IoCComponent
{
   public static final String DESTRUCTION_CALLBACK_NAME_PREFIX = IoCComponent.class.getName()
         + ".DESTRUCTION_CALLBACK.";

   private BeanFactory beanfactory;

   private static final ThreadLocal<ObjectFactory> factoryBean = new ThreadLocal<ObjectFactory>();

   /**
    * Creates a Spring Seam Component given a beanFactory.
    *
    * @param clazz   class
    * @param name    component name
    * @param scope   component scope
    * @param factory factory
    */
   public SpringComponent(Class clazz, String name, ScopeType scope, BeanFactory factory)
   {
      super(clazz, name, scope);
      this.beanfactory = factory;
   }

   public static ObjectFactory getFactoryBean()
   {
      return factoryBean.get();
   }

   public static void setFactoryBean(ObjectFactory bean)
   {
      factoryBean.set(bean);
   }

   protected String getIoCName()
   {
      return "Spring";
   }

   protected Object instantiateIoCBean() throws Exception
   {
      ObjectFactory objectFactory = getFactoryBean();
      if (objectFactory == null)
      {
         return beanfactory.getBean(getName());
      }
      setFactoryBean(null);
      return objectFactory.getObject();
   }

   /**
    * Calls the spring destroy callback when seam destroys the component
    *
    * @see org.jboss.seam.Component#callDestroyMethod(Object)
    */
   @Override
   public void callDestroyMethod(Object instance)
   {
      super.callDestroyMethod(instance);
      // Cannot call the callback on a STATELESS bean
      if (getScope() != ScopeType.STATELESS)
      {
         Runnable callback = (Runnable)getScope().getContext().get(DESTRUCTION_CALLBACK_NAME_PREFIX + getName());
         if (callback != null)
         {
            callback.run();
         }
      }
   }

   /**
    * Registers a destruction callback with this bean.
    *
    * @param name    bean name
    * @param destroy the destroy to set
    */
   public void registerDestroyCallback(String name, Runnable destroy)
   {
      // Not sure yet how to register a stateless bean's Destruction callback.
      if (getScope() != ScopeType.STATELESS)
      {
         getScope().getContext().set(DESTRUCTION_CALLBACK_NAME_PREFIX + name, destroy);
		}
	}

}
