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
 * @author Christian Bauer
 */
@Name("org.jboss.seam.resteasy.dispatcher")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "resteasyBootstrap")
@AutoCreate
@Install(classDependencies = "org.resteasy.Dispatcher")
public class ResteasyDispatcher extends HttpServletDispatcher {

    @In
    RestApplicationConfig restApplicationConfig;

    @Logger
    Log log;

    @Create
    public void onStartup() {
        log.info("registering RESTEasy resources and providers");

        ResteasyProviderFactory providerFactory = new ResteasyProviderFactory();
        ResteasyProviderFactory.setInstance(providerFactory); // This is really necessary
        setDispatcher(new Dispatcher(providerFactory));

        getDispatcher().setLanguageMappings(restApplicationConfig.getLanguageMappings());
        getDispatcher().setMediaTypeMappings(restApplicationConfig.getMediaTypeMappings());

        // Resource registration
        Registry registry = getDispatcher().getRegistry();
        for (final Class resourceClass : restApplicationConfig.getResourceClasses()) {
            final Component seamComponent = restApplicationConfig.getResourceClassComponent(resourceClass);
            if (seamComponent != null) {
                // Seam component lookup when call is dispatched to resource
                registry.addResourceFactory(
                    new ResourceFactory() {

                        private PropertyInjector propertyInjector;

                        public Class<?> getScannableClass() {
                            return resourceClass;
                        }

                        public void registered(InjectorFactory factory) {
                            this.propertyInjector = factory.createPropertyInjector(getScannableClass());
                        }

                        public Object createResource(HttpRequest request, HttpResponse response, InjectorFactory factory) {
                            Object target = Component.getInstance(seamComponent.getName());
                            propertyInjector.inject(request, response, target);
                            return target;
                        }

                        public void requestFinished(HttpRequest request, HttpResponse response, Object resource) {}
                        public void unregistered() {}
                    }
                );
            } else {
                // JAX-RS default lifecycle
                registry.addResourceFactory(new POJOResourceFactory(resourceClass));
            }
        }

        // Provider registration
        if (restApplicationConfig.isUseBuiltinProviders()) {
            log.info("registering built-in RESTEasy providers");
            RegisterBuiltin.register(providerFactory);
        }
        for (Class providerClass : restApplicationConfig.getProviderClasses()) {
            Component seamComponent = restApplicationConfig.getProviderClassComponent(providerClass);
            if (seamComponent != null) {
                if (ScopeType.STATELESS.equals(seamComponent.getScope())) {
                    throw new RuntimeException(
                        "Registration of STATELESS Seam components as RESTEasy providers not implemented!"
                    );
                } else if (ScopeType.APPLICATION.equals(seamComponent.getScope())) {
                    Object providerInstance = Component.getInstance(seamComponent.getName());
                    providerFactory.registerProviderInstance(providerInstance);
                }
            } else {
                // Just plain RESTEasy, no Seam component lookup or lifecycle
                providerFactory.registerProvider(providerClass);
            }
        }

    }
}
