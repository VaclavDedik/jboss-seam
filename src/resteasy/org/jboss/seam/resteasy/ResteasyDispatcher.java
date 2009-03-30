package org.jboss.seam.resteasy;

import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.log.Log;
import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.*;

/**
 * An extended version of the RESTEasy dispatcher, configured on Seam application
 * startup with a custom JAX RS <tt>Application</tt> instance. Registers custom resource
 * and provider lifecycle handlers.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.dispatcher")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "org.jboss.seam.resteasy.bootstrap")
@AutoCreate
@Install(classDependencies = "org.jboss.resteasy.core.Dispatcher")
public class ResteasyDispatcher extends HttpServletDispatcher
{

    @In
    Application application;

    @Logger
    Log log;

    @Create
    public void onStartup()
    {
        log.debug("registering RESTEasy and JAX RS resources and providers");

        ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
        ResteasyProviderFactory.setInstance(providerFactory); // This is really necessary
        setDispatcher(new AsynchronousDispatcher(providerFactory));

        getDispatcher().setLanguageMappings(application.getLanguageMappings());
        getDispatcher().setMediaTypeMappings(application.getMediaTypeMappings());

        // Provider registration
        if (application.isUseBuiltinProviders())
        {
            log.info("registering built-in RESTEasy providers");
            RegisterBuiltin.register(providerFactory);
        }
        for (Class providerClass : application.getProviderClasses())
        {
            Set<Component> seamComponents = application.getProviderClassComponent(providerClass);
            if (seamComponents != null)
            {
                for (Component seamComponent : seamComponents)
                {
                   if (ScopeType.STATELESS.equals(seamComponent.getScope()))
                   {
                      throw new RuntimeException(
                               "Registration of STATELESS Seam components as RESTEasy providers not implemented!"
                      );
                   }
                   else if (ScopeType.APPLICATION.equals(seamComponent.getScope()))
                   {
                      Object providerInstance = Component.getInstance(seamComponent.getName());
                      boolean isStringConverter = false;
                      for (Class componentIface : seamComponent.getBusinessInterfaces())
                      {
                         if (StringConverter.class.isAssignableFrom(componentIface))
                         {
                            isStringConverter = true;
                            break;
                         }
                      }
                      if (isStringConverter)
                      {
                         log.error("can't register Seam component as RESTEasy StringConverter, see: https://jira.jboss.org/jira/browse/JBSEAM-4020");
                         //log.debug("registering Seam component as custom RESTEasy string converter provider: " + seamComponent.getName());
                         //providerFactory.addStringConverter((StringConverter)providerInstance);
                      }
                      else
                      {
                         providerFactory.registerProviderInstance(providerInstance);
                      }
                   }
                }
            }
            else
            {
                // Just plain RESTEasy, no Seam component lookup or lifecycle
                if (StringConverter.class.isAssignableFrom(providerClass))
                {
                    log.debug("registering as custom RESTEasy string converter provider class: " + providerClass);
                    providerFactory.addStringConverter(providerClass);
                }
                else
                {
                    providerFactory.registerProvider(providerClass);
                }
            }
        }

        // Resource registration
        Registry registry = getDispatcher().getRegistry();
        for (final Class resourceClass : application.getClasses())
        {
            log.debug("registering JAX RS resource class: " + resourceClass);
            Set<Component> components = application.getResourceClassComponent(resourceClass);
            if (components != null)
            {
                log.debug("registering all {0} components of {1}", components.size(), resourceClass);
                // Register every component
                for (final Component seamComponent : components)
                {
                    // Seam component lookup when call is dispatched to resource
                    ResourceFactory factory = new ResourceFactory()
                        {

                            public Class<?> getScannableClass()
                            {
                                return resourceClass;
                            }

                            public void registered(InjectorFactory factory)
                            {
                                // Wrap the Resteasy PropertyInjectorImpl in a Seam interceptor (for @Context injection)
                                seamComponent.addInterceptor(
                                        new ResteasyContextInjectionInterceptor(
                                                new PropertyInjectorImpl(getScannableClass(), dispatcher.getProviderFactory())
                                        )
                                );

                                // NOTE: Adding an interceptor to Component at this stage means that the interceptor is
                                // always executed last in the chain. The sorting of interceptors of a Component occurs
                                // only when the Component metadata is instantiated. This is OK in this case, as the
                                // JAX RS @Context injection can occur last after all other interceptors executed.

                            }

                            public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
                            {
                                // Push this onto event context so we have it available in ResteasyContextInjectionInterceptor
                                Contexts.getEventContext().set(ResteasyContextInjectionInterceptor.RE_HTTP_REQUEST_VAR, request);
                                Contexts.getEventContext().set(ResteasyContextInjectionInterceptor.RE_HTTP_RESPONSE_VAR, response);
                                return Component.getInstance(seamComponent.getName());
                            }

                            public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
                            {
                            }

                            public void unregistered()
                            {
                            }
                        };
                    // Register on specific path if component has it's path property set to not-null value
                    if (AbstractResource.class.isAssignableFrom(seamComponent.getBeanClass()))
                    {
                       // TODO get the path some other way - it may not be possible to create a instance at this time
                       AbstractResource instance = (AbstractResource) seamComponent.newInstance();
                       String path = instance.getPath();
                       if (instance.getPath() != null)
                       {
                          log.debug("registering resource {0} on path {1}", seamComponent.getName(), path);
                          registry.addResourceFactory(factory, path);
                       }
                       else
                       {
                          log.debug("registering resource {0}", seamComponent.getName());
                          registry.addResourceFactory(factory);
                       }
                    }
                    else
                    {
                       log.debug("registering resource {0}", seamComponent.getName());
                       registry.addResourceFactory(factory);
                    }
                }
            }
            else
            {
               // ResourceHome and ResourceQuery won't be registered if not declared as a component
               if (ResourceHome.class.equals(resourceClass) || ResourceQuery.class.equals(resourceClass))
                  continue;
               
                // JAX-RS default lifecycle
                log.info("registering resource {0} with default JAX-RS lifecycle", resourceClass);
                registry.addResourceFactory(new POJOResourceFactory(resourceClass));
            }
        }

    }
}
