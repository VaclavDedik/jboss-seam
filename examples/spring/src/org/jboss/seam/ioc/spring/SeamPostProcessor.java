package org.jboss.seam.ioc.spring;

import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.ClassUtils;

/**
 * Post processor that makes all of the seam scopes available in spring and
 * takes all of the beans with those scopes and creates Seam Components out of
 * them.
 * <p/>
 * To use simply define this bean in one of your context files.
 *
 * @author youngm
 */
public class SeamPostProcessor implements BeanFactoryPostProcessor, InitializingBean
{
   private static final LogProvider log = Logging.getLogProvider(SeamPostProcessor.class);

   /**
    * Default seam scope prefix.
    */
   public static final String DEFAULT_SCOPE_PREFIX = "seam.";

   private String scopePrefix;

   /**
    * Null is not a valid scopePrefix so make it the default is used if null or empty.
    *
    * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
    */
   public void afterPropertiesSet() throws Exception
   {
      if (scopePrefix == null || "".equals(scopePrefix))
      {
         scopePrefix = DEFAULT_SCOPE_PREFIX;
      }
   }

   /**
    * Add all of the seam scopes to this beanFactory.
    *
    * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
    */
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
   {

      for (ScopeType scope : ScopeType.values())
      {
         // Don't create a scope for UnSpecified
         if (scope != ScopeType.UNSPECIFIED)
         {
            beanFactory.registerScope(scopePrefix + scope.name(), new SeamScope(scope));
         }
      }
      // Create a mock application context if not available.
      boolean unmockApplication = false;
      if (!Contexts.isApplicationContextActive())
      {
         Lifecycle.mockApplication();
         unmockApplication = true;
      }
      try
      {
         Context applicationContext = Contexts.getApplicationContext();
         // Iterate through all the beans in the factory
         for (String beanName : beanFactory.getBeanDefinitionNames())
         {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
            Class beanClass;
            ScopeType scope;
            if (definition.getScope().startsWith(scopePrefix))
            {
               scope = ScopeType.valueOf(definition.getScope().replaceFirst(scopePrefix, "").toUpperCase());
            }
            else
            {
               if (log.isDebugEnabled())
               {
                  log.debug("No scope could be derived for bean with name: " + beanName);
               }
               continue;
            }
            if (scope == ScopeType.UNSPECIFIED)
            {
               if (log.isDebugEnabled())
               {
                  log.debug("Discarding bean with scope UNSPECIFIED.  Spring will throw an appropriate error later: " + beanName);
               }
               continue;
            }
            // Cannot be a seam component without a class maybe later
            if (definition.getBeanClassName() == null)
            {
               throw new FatalBeanException("Seam scoped bean must explicitly define a class.");
            }
            else
            {
               try
               {
                  beanClass = ClassUtils.forName(definition.getBeanClassName());
               }
               catch (ClassNotFoundException e)
               {
                  throw new FatalBeanException("Error", e);
               }
            }
            // Add the component to seam
            applicationContext.set(beanName + Initialization.COMPONENT_SUFFIX, new SpringComponent(beanClass, beanName, scope, beanFactory));
         }
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
    * @param scopePrefix the prefix to use to identify seam scopes for spring
    *                    beans
    */
   public void setScopePrefix(String scopePrefix)
   {
      this.scopePrefix = scopePrefix;
   }
}
