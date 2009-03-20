package org.jboss.seam.resteasy;

import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.core.BijectionInterceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.Component;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * Runs after Seam injection and provides JAX RS @Context handling, required for
 * field injection on the actual bean (not proxy) instance.
 *
 * @author Christian Bauer
 */
@Interceptor(stateless=true)
public class ResteasyContextInjectionInterceptor extends AbstractInterceptor
{

    public static final String RE_HTTP_REQUEST_VAR = "org.jboss.resteasy.spi.HttpRequest";
    public static final String RE_HTTP_RESPONSE_VAR = "org.jboss.resteasy.spi.HttpResponse";

    private final PropertyInjector propertyInjector;

    public ResteasyContextInjectionInterceptor(PropertyInjector propertyInjector)
    {
        this.propertyInjector = propertyInjector;
    }

    public Object aroundInvoke(InvocationContext ic) throws Exception
    {
        HttpRequest request = (HttpRequest) Component.getInstance(RE_HTTP_REQUEST_VAR);
        HttpResponse response = (HttpResponse)Component.getInstance(RE_HTTP_RESPONSE_VAR);

        propertyInjector.inject(request, response, ic.getTarget());

        return ic.proceed();
    }

    public boolean isInterceptorEnabled()
    {
        return true;
    }
}
