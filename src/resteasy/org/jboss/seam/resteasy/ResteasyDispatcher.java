package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
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
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.*;
import org.jboss.resteasy.util.Types;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

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
            log.debug("registering JAX RS provider class: " + providerClass.getName());
            Component seamComponent = application.getProviderClassComponent(providerClass);
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
                    boolean isStringConverter = false;
                    for (Class componentIface : seamComponent.getBusinessInterfaces())
                    {
                        if (StringConverter.class.isAssignableFrom(componentIface)) {
                            isStringConverter = true;
                            break;
                        }
                    }
                    if (isStringConverter) {
                        log.error("can't register Seam component as RESTEasy StringConverter, see: https://jira.jboss.org/jira/browse/JBSEAM-4020");
                        //log.debug("registering Seam component as custom RESTEasy string converter provider: " + seamComponent.getName());
                        //providerFactory.addStringConverter((StringConverter)providerInstance);
                    } else {
                        providerFactory.registerProviderInstance(providerInstance);
                    }
                }
            }
            else
            {
                // Just plain RESTEasy, no Seam component lookup or lifecycle
                if (StringConverter.class.isAssignableFrom(providerClass)) {
                    log.debug("registering as custom RESTEasy string converter provider class: " + providerClass);
                    providerFactory.addStringConverter(providerClass);
                } else {
                    providerFactory.registerProvider(providerClass);
                }
            }
        }

        // Resource registration
        Registry registry = getDispatcher().getRegistry();
        for (final Class resourceClass : application.getClasses())
        {
            final Component seamComponent = application.getResourceClassComponent(resourceClass);
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

    }
}
