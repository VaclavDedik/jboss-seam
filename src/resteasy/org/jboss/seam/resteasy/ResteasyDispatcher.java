package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.spi.*;

/**
 * An extended version of the RESTEasy dispatcher, configured on Seam application
 * startup with a custom ApplicationConfig instance. Registers custom resource
 * and provider lifecycle handlers.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.dispatcher")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "resteasyBootstrap")
@AutoCreate
@Install(classDependencies = "org.resteasy.Dispatcher")
public class ResteasyDispatcher extends HttpServletDispatcher
{

    @In
    ApplicationConfig applicationConfig;

    @Logger
    Log log;

    @Create
    public void onStartup()
    {
        log.debug("assigning registered RESTEasy resources and providers");

        ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
        ResteasyProviderFactory.setInstance(providerFactory); // This is really necessary
        setDispatcher(new Dispatcher(providerFactory));

        getDispatcher().setLanguageMappings(applicationConfig.getLanguageMappings());
        getDispatcher().setMediaTypeMappings(applicationConfig.getMediaTypeMappings());

        // Resource registration
        Registry registry = getDispatcher().getRegistry();
        for (final Class resourceClass : applicationConfig.getResourceClasses())
        {
            final Component seamComponent = applicationConfig.getResourceClassComponent(resourceClass);
            if (seamComponent != null)
            {
                // Seam component lookup when call is dispatched to resource
                registry.addResourceFactory(
                        new ResourceFactory()
                        {

                            private PropertyInjector propertyInjector;

                            public Class<?> getScannableClass()
                            {
                                return resourceClass;
                            }

                            public void registered(InjectorFactory factory)
                            {
                                this.propertyInjector = factory.createPropertyInjector(getScannableClass());
                            }

                            public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory)
                            {
                                Object target = Component.getInstance(seamComponent.getName());
                                propertyInjector.inject(request, response, target);
                                return target;
                            }

                            public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
                            {
                            }

                            public void unregistered()
                            {
                            }
                        }
                );
            }
            else
            {
                // JAX-RS default lifecycle
                registry.addResourceFactory(new POJOResourceFactory(resourceClass));
            }
        }

        // Provider registration
        if (applicationConfig.isUseBuiltinProviders())
        {
            log.info("registering built-in RESTEasy providers");
            RegisterBuiltin.register(providerFactory);
        }
        for (Class providerClass : applicationConfig.getProviderClasses())
        {
            Component seamComponent = applicationConfig.getProviderClassComponent(providerClass);
            if (seamComponent != null)
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
                    providerFactory.registerProviderInstance(providerInstance);
                }
            }
            else
            {
                // Just plain RESTEasy, no Seam component lookup or lifecycle
                providerFactory.registerProvider(providerClass);
            }
        }

    }
}
