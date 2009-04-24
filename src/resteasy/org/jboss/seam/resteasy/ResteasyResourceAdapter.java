package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractResource;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.ThreadLocalResteasyProviderFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.PathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Accepts incoming HTTP request throug the SeamResourceServlet and
 * dispatches the call to RESTEasy. Wraps the call in Seam contexts.
 *
 * @author Christian Bauer
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.resteasy.resourceAdapter")
@BypassInterceptors
public class ResteasyResourceAdapter extends AbstractResource
{

    @Logger
    Log log;

    protected ResteasyDispatcher dispatcher;
    protected Application application;

    @Create
    public void init()
    {
        // No injection, so lookup on first request
        dispatcher = (ResteasyDispatcher) Component.getInstance(ResteasyDispatcher.class);
        application = (Application) Component.getInstance(Application.class);
    }

    @Override
    public String getResourcePath()
    {
        Application appConfig = (Application) Component.getInstance(Application.class);
        return appConfig.getResourcePathPrefix();
    }

    @Override
    public void getResource(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {

        try
        {
            log.debug("processing REST request");

            // Wrap in RESTEasy thread-local factory handling
            ThreadLocalResteasyProviderFactory.push(dispatcher.getDispatcher().getProviderFactory());

            // Wrap in RESTEasy contexts (this also puts stuff in a thread-local)
            SeamResteasyProviderFactory.pushContext(HttpServletRequest.class, request);
            SeamResteasyProviderFactory.pushContext(HttpServletResponse.class, response);
            SeamResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(request));

            // Wrap in Seam contexts
            new ContextualHttpServletRequest(request)
            {
                @Override
                public void process() throws ServletException, IOException
                {

                    HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
                    UriInfoImpl uriInfo = extractUriInfo(request);

                    HttpResponse theResponse = new HttpServletResponseWrapper(
                            response,
                            dispatcher.getDispatcher().getProviderFactory()
                    );

                    HttpRequest in = new HttpServletInputMessage(
                            request,
                            theResponse,
                            headers,
                            uriInfo,
                            request.getMethod().toUpperCase(),
                            (SynchronousDispatcher) dispatcher.getDispatcher()
                    );

                    dispatcher.getDispatcher().invoke(in, theResponse);
                }
            }.run();

        }
        finally
        {
            // Clean up the thread-locals
            SeamResteasyProviderFactory.clearContextData();
            ThreadLocalResteasyProviderFactory.pop();
            log.debug("completed processing of REST request");
        }
    }

    // Replaces the static ServletUtil.extractUriInfo(), removes the Seam-related sub-path
    protected UriInfoImpl extractUriInfo(HttpServletRequest request)
    {
        String contextPath = request.getContextPath();
        URI absolutePath;
        try
        {
            URL absolute = new URL(request.getRequestURL().toString());

            UriBuilderImpl builder = new UriBuilderImpl();
            builder.scheme(absolute.getProtocol());
            builder.host(absolute.getHost());
            builder.port(absolute.getPort());
            builder.path(absolute.getPath());
            builder.replaceQuery(absolute.getQuery());
            absolutePath = builder.build();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        String path = PathHelper.getEncodedPathInfo(absolutePath.getRawPath(), contextPath);

        if (application.isStripSeamResourcePath())
        {
            log.debug("removing SeamResourceServlet url-pattern and dispatcher prefix from request path");
            path = path.substring(path.indexOf(getResourcePath()) + getResourcePath().length());
        }

        List<PathSegment> pathSegments = PathSegmentImpl.parseSegments(path);
        URI baseURI = absolutePath;
        if (!path.trim().equals(""))
        {
            String tmpContextPath = contextPath;
            if (!tmpContextPath.endsWith("/")) tmpContextPath += "/";
            baseURI = UriBuilder.fromUri(absolutePath).replacePath(tmpContextPath).build();
        }

        log.debug("UriInfo, absolute URI       : " + absolutePath);
        log.debug("UriInfo, base URI           : " + baseURI);
        log.debug("UriInfo, relative path/@Path: " + path);
        log.debug("UriInfo, query string       : " + request.getQueryString());

        return new UriInfoImpl(absolutePath, baseURI, path, request.getQueryString(), pathSegments);
    }
}
