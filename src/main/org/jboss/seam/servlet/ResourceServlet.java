package org.jboss.seam.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.PhaseId;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;

/**
 * Serves extra resources such as Javascript, CSS, Images
 * 
 * @author Shane Bryzak
 */
public class ResourceServlet extends HttpServlet
{
   private ServletContext context;

   private Map<String, AbstractResource> providers = new HashMap<String, AbstractResource>();

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);
      context = config.getServletContext();
      loadResourceProviders();
   }

   protected void loadResourceProviders()
   {
      Context tempApplicationContext = new WebApplicationContext(context);

      Init init = (Init) tempApplicationContext.get(Init.class);
      for (String name : init.getResourceProviders())
      {
         AbstractResource provider = (AbstractResource) tempApplicationContext.get(name);
         if (provider != null)
         {
            provider.setServletContext(context);
            providers.put(provider.getResourcePath(), provider);
         }
      }
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
   {
      String prefix = request.getContextPath() + request.getServletPath();

      if (request.getRequestURI().startsWith(prefix))
      {
         String path = request.getRequestURI().replaceFirst(prefix, "");
         int index = path.indexOf('/', 1);
         if (index != -1) path = path.substring(0, index);

         AbstractResource provider = providers.get(path);
         if (provider != null)
         {
            // Set up Seam contexts for Resource Providers
            HttpSession session = request.getSession(true);
            Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
            Lifecycle.setServletRequest(request);
            Lifecycle.beginRequest(getServletContext(), session, request);
            Manager.instance().restoreConversation(request.getParameterMap());
            Lifecycle.resumeConversation(session);
            Manager.instance().handleConversationPropagation(request.getParameterMap());
            try
            {
               provider.getResource(request, response);
               // TODO: conversation timeout
               Manager.instance().endRequest(ContextAdaptor.getSession(session));
               Lifecycle.endRequest(session);
            }
            catch (Exception e)
            {
               Lifecycle.endRequest();
               throw new ServletException(e);
            }
            finally
            {
               Lifecycle.setServletRequest(null);
               Lifecycle.setPhaseId(null);
            }
         }

      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
   {
      throw new UnsupportedOperationException("Cannot post to the resource servlet");
   }
}
