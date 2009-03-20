package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractResource;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.jboss.resteasy.plugins.server.servlet.ServletUtil;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.PathHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;
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

    @Override
    public String getResourcePath()
    {
        Application appConfig = (Application)Component.getInstance(Application.class);
        return appConfig.getResourcePathPrefix();
    }

    @Override
    public void getResource(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {

        // Wrap in RESTEasy contexts
        try {
            log.debug("processing REST request");
            ResteasyProviderFactory.pushContext(HttpServletRequest.class, request);
            ResteasyProviderFactory.pushContext(HttpServletResponse.class, response);
            ResteasyProviderFactory.pushContext(SecurityContext.class, new ServletSecurityContext(request));

            // Wrap in Seam contexts
            new ContextualHttpServletRequest(request)
            {
                @Override
                public void process() throws ServletException, IOException
                {

                    HttpHeaders headers = ServletUtil.extractHttpHeaders(request);
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

                    String path = PathHelper.getEncodedPathInfo(absolutePath.getRawPath(), request.getContextPath());

                    Application appConfig = (Application)Component.getInstance(Application.class);
                    if (appConfig.isStripSeamResourcePath()) {
                        log.trace("removing SeamResourceServlet url-pattern and dispatcher prefix from request path");
                        path = path.substring(path.indexOf(getResourcePath())+getResourcePath().length());
                    }

                    log.debug("final request path: " + path);
                    List<PathSegment> pathSegments = PathSegmentImpl.parseSegments(path);
                    UriInfoImpl uriInfo = new UriInfoImpl(absolutePath, path, request.getQueryString(), pathSegments);

                    HttpRequest in;
                    ResteasyDispatcher dispatcher =
                            (ResteasyDispatcher) Component.getInstance(ResteasyDispatcher.class);
                    HttpResponse theResponse =
                            new HttpServletResponseWrapper(response, dispatcher.getDispatcher().getProviderFactory());
                    in = new HttpServletInputMessage( 
                          request, theResponse, headers, uriInfo, request.getMethod().toUpperCase(), (SynchronousDispatcher)dispatcher.getDispatcher());
                    
                    dispatcher.getDispatcher().invoke(in, theResponse);
                }
            }.run();

        } finally {
            ResteasyProviderFactory.clearContextData();
            log.debug("completed processing of REST request");
        }
    }
}
