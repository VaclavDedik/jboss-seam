package org.jboss.seam.remoting;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.AbstractResource;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.remoting.remotingResourceProvider")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class Remoting extends AbstractResource
{   
   /**
    * We use a Map for this because a Servlet can serve requests for more than
    * one context path.
    */
   private Map<String, byte[]> cachedConfig = new HashMap<String, byte[]>();
   
   private static final LogProvider log = Logging.getLogProvider(Remoting.class);

   private static final Pattern pathPattern = Pattern.compile("/(.*?)/([^/]+)");

   private static final String REMOTING_RESOURCE_PATH = "resource";   
      
   @Override
   protected String getResourcePath()
   {
      return "/remoting";
   }
   
   private synchronized void initConfig(String contextPath,
            HttpSession session, HttpServletRequest request)
   {
      if (!cachedConfig.containsKey(contextPath))
      {
         try
         {
            Lifecycle.beginRequest(getServletContext(), session, request);

            StringBuilder sb = new StringBuilder();
            sb.append("\nSeam.Remoting.resourcePath = \"");
            sb.append(contextPath);
            sb.append(request.getServletPath());
            sb.append(getResourcePath());
            sb.append("\";");
            sb.append("\nSeam.Remoting.debug = ");
            sb.append(RemotingConfig.instance().getDebug() ? "true" : "false");
            sb.append(";");
            sb.append("\nSeam.Remoting.pollInterval = ");
            sb.append(RemotingConfig.instance().getPollInterval());
            sb.append(";");
            sb.append("\nSeam.Remoting.pollTimeout = ");
            sb.append(RemotingConfig.instance().getPollTimeout());
            sb.append(";");

            cachedConfig.put(contextPath, sb.toString().getBytes());
         }
         finally
         {
            Lifecycle.endRequest(session);
         }
      }
   }   
   
   @Override
   public void getResource(HttpServletRequest request, HttpServletResponse response)
       throws IOException
   {
      try
      {         
         String pathInfo = request.getPathInfo().substring(getResourcePath().length());      
         
         RequestHandler handler = RequestHandlerFactory.getInstance()
               .getRequestHandler(pathInfo);
         if (handler != null)
         {
            handler.setServletContext(getServletContext());
            handler.handle(request, response);
         }
         else
         {
            Matcher m = pathPattern.matcher(pathInfo);
            if (m.matches())
            {
               String path = m.group(1);
               String resource = m.group(2);
               HttpSession session = request.getSession();

               if (REMOTING_RESOURCE_PATH.equals(path))
               {
                  writeResource(resource, response.getOutputStream());
                  if ("remote.js".equals(resource))
                  {
                     appendConfig(response.getOutputStream(), request
                           .getContextPath(), session, request);
                  }
               }
               response.getOutputStream().flush();               
            }
         }
      }
      catch (Exception ex)
      {
         log.error("Error", ex);
      }      
   }
   

   /**
    * Appends various configuration options to the remoting javascript client
    * api.
    * 
    * @param out OutputStream
    */
   private void appendConfig(OutputStream out, String contextPath,
         HttpSession session, HttpServletRequest request) throws IOException
   {
      if (!cachedConfig.containsKey(contextPath))
         initConfig(contextPath, session, request);

      out.write(cachedConfig.get(contextPath));
   }   

   /**
    * 
    * @param resourceName String
    * @param out OutputStream
    */
   private void writeResource(String resourceName, OutputStream out)
         throws IOException
   {
      // Only allow resource requests for .js files
      if (resourceName.endsWith(".js"))
      {
         InputStream in = this.getClass().getClassLoader().getResourceAsStream(
               "org/jboss/seam/remoting/" + resourceName);

         if (in != null)
         {
            byte[] buffer = new byte[1024];
            int read = in.read(buffer);
            while (read != -1)
            {
               out.write(buffer, 0, read);
               read = in.read(buffer);
            }
         }
         else
            log.error(String.format("Resource [%s] not found.", resourceName));
      }
   }   
}
