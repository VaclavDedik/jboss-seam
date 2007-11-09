package org.jboss.seam.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.web.AbstractResource;

/**
 * Serves extra resources such as Javascript, CSS, Images
 * 
 * @author Shane Bryzak
 */
public class SeamResourceServlet extends HttpServlet
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
      try
      {
         Lifecycle.mockApplication();

         for (String name : Init.instance().getResourceProviders())
         {         
            AbstractResource provider = (AbstractResource) Component.getInstance(name, true);
            if (provider != null)
            {
               provider.setServletContext(context);
               providers.put( provider.getResourcePath(), provider );
            }
         }
      }
      finally
      {
         Lifecycle.unmockApplication();
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
            provider.getResource(request, response);            
         }
         else
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
      }
      else
      {
         response.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
   }

   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
   {
      doGet(request, response);
   }
}
