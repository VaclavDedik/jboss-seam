package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.deployment.AnnotationDeploymentHandler;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.EJB;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Scans annoated JAX-RS resources and providers, optionally registers them as Seam components.
 * It does so by populating the <tt>Application</tt> instance, which is then processed further
 * by the <tt>ResteasyDispatcher</tt> during startup.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.bootstrap")
@Scope(ScopeType.APPLICATION)
@Startup
@AutoCreate
@Install(classDependencies = "org.jboss.resteasy.spi.ResteasyProviderFactory")
public class ResteasyBootstrap
{

    @Logger
    Log log;

    @In
    protected Application application;

    private SeamResteasyProviderFactory providerFactory;
    public SeamResteasyProviderFactory getProviderFactory()
    {
        return providerFactory;
    }

    @Create
    public void init()
    {
        log.info("starting RESTEasy with custom SeamResteasyProviderFactory");
        providerFactory = new SeamResteasyProviderFactory();

        // Always use the "deployment sensitive" factory - that means it is handled through ThreadLocal, not static
        SeamResteasyProviderFactory.setInstance(new ThreadLocalResteasyProviderFactory(getProviderFactory()));

        log.info("deploying JAX-RS application");

        Collection<Class<?>> annotatedProviderClasses = null;
        Collection<Class<?>> annotatedResourceClasses = null;
        if (application.isScanProviders() || application.isScanResources())
        {
            log.debug("scanning all classes for JAX-RS annotations");

            DeploymentStrategy deployment = (DeploymentStrategy) Component.getInstance("deploymentStrategy");
            AnnotationDeploymentHandler handler =
                    (AnnotationDeploymentHandler) deployment.getDeploymentHandlers().get(AnnotationDeploymentHandler.NAME);

            annotatedProviderClasses = handler.getClassMap().get(javax.ws.rs.ext.Provider.class.getName());
            annotatedResourceClasses = handler.getClassMap().get(javax.ws.rs.Path.class.getName());
        }

        log.debug("finding all Seam component classes");
        Map<Class, Set<Component>> seamComponents = new HashMap<Class, Set<Component>>();
        String[] applicationContextNames = Contexts.getApplicationContext().getNames();
        for (String applicationContextName : applicationContextNames)
        {
            if (applicationContextName.endsWith(".component"))
            {
                Component seamComponent =
                        (Component) Component.getInstance(applicationContextName, ScopeType.APPLICATION);
                // TODO: This should consider EJB components/annotations on interfaces somehow?
                Class beanClass = seamComponent.getBeanClass();
                if (!seamComponents.containsKey(beanClass))
                {
                   seamComponents.put(beanClass, new HashSet<Component>());
                }
                seamComponents.get(beanClass).add(seamComponent);
            }
        }

        registerProviders(seamComponents, annotatedProviderClasses);
        registerResources(seamComponents, annotatedResourceClasses);
    }

    // Load all provider classes, either scanned or through explicit configuration
    protected void registerProviders(Map<Class, Set<Component>> seamComponents, Collection annotatedProviderClasses)
    {
        Collection<Class> providerClasses = new HashSet<Class>();
        try
        {
            if (application.isScanProviders() && annotatedProviderClasses != null)
                providerClasses.addAll(annotatedProviderClasses);

            for (String s : new HashSet<String>(application.getProviderClassNames()))
                providerClasses.add(Reflections.classForName(s));

        }
        catch (ClassNotFoundException ex)
        {
            log.error("error loading JAX-RS provider class: " + ex.getMessage());
        }
        for (Class providerClass : providerClasses)
        {
            // Ignore built-in providers, we register them manually later
            if (providerClass.getName().startsWith("org.jboss.resteasy.plugins.providers")) continue;

            // Check if this is also a Seam component bean class
            if (seamComponents.containsKey(providerClass))
            {
               for (Component seamComponent : seamComponents.get(providerClass))
               {
                  // Needs to be APPLICATION or STATELESS
                  if (!seamComponent.getScope().equals(ScopeType.APPLICATION) &&
                        !seamComponent.getScope().equals(ScopeType.STATELESS))
                  {
                     log.warn("can't add provider Seam component, not APPLICATION or STATELESS scope: " + seamComponent.getName());
                     log.warn("this provider class will be registered without Seam injection or lifecycle!");
                     seamComponent = null;
                  }
                  if (seamComponent != null)
                  {
                     log.debug("adding provider Seam component: " + seamComponent.getName());
                     application.addProviderClass(providerClass, seamComponent);
                  }
                  else
                  {
                     log.debug("adding provider class: " + providerClass.getName());
                     application.addProviderClass(providerClass);
                  }
               }
            }
            else
            {
               log.debug("adding provider class: " + providerClass.getName());
               application.addProviderClass(providerClass);
            }
         }
        if (application.getProviderClasses().size() == 0 &&
                !application.isUseBuiltinProviders())
        {
            log.info("no RESTEasy provider classes added");
        }
    }

    // Load all resource classes, either scanned or through explicit configuration
    protected void registerResources(Map<Class, Set<Component>> seamComponents, Collection annotatedResourceClasses)
    {
        Collection<Class> resourceClasses = new HashSet<Class>();
        try
        {
            if (application.isScanResources() && annotatedResourceClasses != null)
                resourceClasses.addAll(annotatedResourceClasses);

            for (String s : new HashSet<String>(application.getResourceClassNames()))
                resourceClasses.add(Reflections.classForName(s));

        }
        catch (ClassNotFoundException ex)
        {
            log.error("error loading JAX-RS resource class: " + ex.getMessage());
        }
        for (Class<Object> resourceClass : resourceClasses)
        {
            // Check if this is also a Seam component bean class
            if (seamComponents.containsKey(resourceClass))
            {
                Set<Component> components = seamComponents.get(resourceClass);
                log.debug("adding resource Seam components {0} for class {1}", components, resourceClass);
                application.addResourceClass(resourceClass, components);
            }
            // Check if it is a @Path annotated EJB interface
            else if (resourceClass.isAnnotationPresent(EJB.LOCAL) ||
                    resourceClass.isAnnotationPresent(EJB.REMOTE))
            {
                log.debug("ignoring @Path annotated EJB interface, add the bean " +
                          "implementation to <resteasy:resource-class-names/>: " + resourceClass.getName());
            }
            else
            {
                log.debug("adding resource class: " + resourceClass.getName());
                application.addResourceClass(resourceClass);
            }
        }
        if (application.getClasses().size() == 0)
            log.info("no JAX-RS resource classes registered");
    }

}
