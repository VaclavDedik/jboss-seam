package org.jboss.seam.resteasy;

import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.core.PropertyInjectorImpl;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

/**
 * Looks up Seam component in Seam contexts when a JAX RS resource is requested.
 *
 * @author Christian Bauer
 */
public class SeamResteasyResourceFactory implements ResourceFactory
{
    Log log = Logging.getLog(SeamResteasyResourceFactory.class);

    private final Class<?> resourceClass;
    private final Component seamComponent;
    private final ResteasyProviderFactory providerFactory;

    public SeamResteasyResourceFactory(Class<?> resourceClass, Component seamComponent, ResteasyProviderFactory providerFactory)
    {
        this.resourceClass = resourceClass;
        this.seamComponent = seamComponent;
        this.providerFactory = providerFactory;
    }

    public Class<?> getScannableClass()
    {
        return resourceClass;
    }

    public void registered(InjectorFactory factory)
    {
        // Wrap the Resteasy PropertyInjectorImpl in a Seam interceptor (for @Context injection)
        seamComponent.addInterceptor(
                new ResteasyContextInjectionInterceptor(
                        new PropertyInjectorImpl(getScannableClass(), providerFactory)
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
        log.debug("creating RESTEasy resource instance by looking up Seam component: " + seamComponent.getName());
        return Component.getInstance(seamComponent.getName());
    }

    public void requestFinished(HttpRequest request, HttpResponse response, Object resource)
    {
    }

    public void unregistered()
    {
    }

}
