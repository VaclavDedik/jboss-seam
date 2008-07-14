package org.jboss.seam.resteasy;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractResource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Accepts incoming HTTP request under the Seam resource-servlet URL mapping and
 * dispatches the call to RESTEasy. Wraps the call in Seam contexts.
 * <p>
 * Hardcoded URL path is <tt>/rest</tt>. Subclass and override the
 * <tt>getResourcePath()</tt> method to customize this.
 * </p>
 *
 * @author Christian Bauer
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.resteasy.resourceAdapter")
@BypassInterceptors
public class ResteasyResourceAdapter extends AbstractResource {

    public static final String RESTEASY_RESOURCE_BASEPATH = "/rest";

    @Override
    public String getResourcePath() {
        return RESTEASY_RESOURCE_BASEPATH;
    }

    @Override
    public void getResource(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        // Wrap this in Seam contexts
        new ContextualHttpServletRequest(request) {
            @Override
            public void process() throws ServletException, IOException {

                ResteasyDispatcher dispatcher =
                        (ResteasyDispatcher)Component.getInstance(ResteasyDispatcher.class);
                if (dispatcher == null) {
                    throw new IllegalStateException("RESTEasy is not installed, check your classpath");
                }
                dispatcher.invoke(
                    new HttpServletRequestWrapper(request) {
                        // TODO: Strip out the /seam/resource/rest stuff
                        public String getPathInfo() {
                            return super.getPathInfo();
                        }
                    },
                    response
                );
            }
        }.run();
    }
}
