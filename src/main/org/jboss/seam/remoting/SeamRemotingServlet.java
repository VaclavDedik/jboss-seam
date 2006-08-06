package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.servlet.SeamServletFilter;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletRequest;

/**
 * Provides remoting capabilities for Seam.
 *
 * @author Shane Bryzak
 */
public class SeamRemotingServlet extends HttpServlet
{
  private static final Log log = LogFactory.getLog(SeamServletFilter.class);

  private static final Pattern pathPattern = Pattern.compile("/(.*?)/([^/]+)");

  private static final String PARAM_ALLOWABLE_TOPICS = "allowableTopics";

  private static final String RESOURCE_PATH = "resource";

  private ServletContext servletContext;

  /**
   *  We use a Map for this because a Servlet can serve
   *  requests for more than one context path.
   */
  private Map<String,byte[]> cachedConfig = new HashMap<String,byte[]>();

  /**
   * Initialise the Remoting servlet
   *
   * @param config ServletConfig
   * @throws ServletException
   */
  public void init(ServletConfig config)
      throws ServletException
  {
    servletContext = config.getServletContext();
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request,
                        HttpServletResponse response)
      throws ServletException, IOException
  {
    try
    {
      RequestHandler handler = RequestHandlerFactory.getInstance().
          getRequestHandler(request.getPathInfo());
      if (handler != null)
      {
        handler.setServletContext(servletContext);
        handler.handle(request, response);
      }
      else
      {
        Matcher m = pathPattern.matcher(request.getPathInfo());
        if (m.matches())
        {
          String path = m.group(1);
          String resource = m.group(2);

          if (RESOURCE_PATH.equals(path))
          {
            writeResource(resource, response.getOutputStream());
            if ("remote.js".equals(resource))
            {
              appendConfig(response.getOutputStream(), request.getContextPath(),
                  request.getSession(), request);
            }
          }
        }
      }
    }
    catch (Exception ex)
    {
      log.error("Error", ex);
    }
  }

  /**
   * Appends various configuration options to the remoting javascript client api.
   *
   * @param out OutputStream
   */
  private void appendConfig(OutputStream out, String contextPath,
                            HttpSession session, ServletRequest request)
      throws IOException
  {
    if (!cachedConfig.containsKey(contextPath))
      initConfig(contextPath, session, request);

    out.write(cachedConfig.get(contextPath));
    out.flush();
  }

  /**
   * Initialise the configuration stuff for the specified context path.
   *
   * @param contextPath String
   */
  private synchronized void initConfig(String contextPath, HttpSession session,
                                       ServletRequest request)
  {
    if (!cachedConfig.containsKey(contextPath))
    {
      try
      {
        Lifecycle.beginRequest(servletContext, session, request);

        StringBuilder sb = new StringBuilder();
        sb.append("\nSeam.Remoting.contextPath = \"");
        sb.append(contextPath);
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
          out.flush();
        }
      }
      else
        log.error(String.format("Resource [%s] not found.", resourceName));
    }
  }
}
