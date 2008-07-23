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
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.plugins.server.servlet.HttpServletInputMessage;
import org.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.resteasy.plugins.server.servlet.ServletSecurityContext;
import org.resteasy.specimpl.PathSegmentImpl;
import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.PathHelper;

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
        ApplicationConfig appConfig = (ApplicationConfig)Component.getInstance(ApplicationConfig.class);
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

                    HttpHeaders headers = HttpServletDispatcher.extractHttpHeaders(request);
                    String path = PathHelper.getEncodedPathInfo(request.getRequestURI(), request.getContextPath());
                    URI absolutePath;
                    try
                    {
                        URL absolute = new URL(request.getRequestURL().toString());

                        UriBuilderImpl builder = new UriBuilderImpl();
                        builder.scheme(absolute.getProtocol());
                        builder.host(absolute.getHost());
                        builder.port(absolute.getPort());
                        builder.path(absolute.getPath());
                        builder.replaceQueryParams(absolute.getQuery());
                        absolutePath = builder.build();
                    }
                    catch (MalformedURLException e)
                    {
                        throw new RuntimeException(e);
                    }

                    ApplicationConfig appConfig = (ApplicationConfig)Component.getInstance(ApplicationConfig.class);
                    if (appConfig.isStripSeamResourcePath()) {
                        log.debug("removing SeamResourceServlet url-pattern and dispatcher prefix from request path");
                        path = path.substring(path.indexOf(getResourcePath())+getResourcePath().length());
                    }

                    log.debug("final request path: " + path);
                    List<PathSegment> pathSegments = PathSegmentImpl.parseSegments(path);
                    UriInfoImpl uriInfo = new UriInfoImpl(absolutePath, path, request.getQueryString(), pathSegments);

                    HttpRequest in;
                    try
                    {
                        in =
                            new HttpServletInputMessage(
                                headers,
                                request.getInputStream(),
                                uriInfo,
                                request.getMethod().toUpperCase()
                            );
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }

                    ResteasyDispatcher dispatcher =
                            (ResteasyDispatcher) Component.getInstance(ResteasyDispatcher.class);
                    HttpResponse theResponse =
                            new HttpServletResponseWrapper(response, dispatcher.getDispatcher().getProviderFactory());
                    dispatcher.getDispatcher().invoke(in, theResponse);
                }
            }.run();

        } finally {
            ResteasyProviderFactory.clearContextData();
            log.debug("completed processing of REST request");
        }
    }
}
