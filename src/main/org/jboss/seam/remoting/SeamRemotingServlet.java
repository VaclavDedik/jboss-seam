package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.PhaseId;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.servlet.SeamServletFilter;

/**
 * Provides remoting capabilities for Seam.
 *
 * @author Shane Bryzak
 */
public class SeamRemotingServlet extends HttpServlet
{
  private static Logger log = Logger.getLogger(SeamServletFilter.class);

  private static final Pattern pathPattern = Pattern.compile("/(.*?)/([^/]+)");

  private static final String INTERFACE_PATH = "interface";
  private static final String RESOURCE_PATH = "resource";

  private ServletContext servletContext;

  private InterfaceGenerator generator;

  private ExecutionHandler executor;

  /** @todo Implement caching */
  private Map<String,String> resourceCache = new HashMap<String,String>();

  public SeamRemotingServlet()
  {
    generator = new InterfaceGenerator();
    executor = new ExecutionHandler();
  }

  public void init(ServletConfig config) throws ServletException
  {
    servletContext = config.getServletContext();
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    try
    {
      HttpSession session = ( (HttpServletRequest) request ).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(servletContext, session);
      Manager.instance().restoreConversation( null, request.getParameterMap() );
      Lifecycle.resumeConversation(session);

      if ("/execute".equals(request.getPathInfo()))
      {
        executor.handle(request, response);
      }
      else if ("/interface.js".equals(request.getPathInfo()))
      {
        String[] componentNames = request.getQueryString().split("&");
        Component[] components = new Component[componentNames.length];
        for (int i = 0; i < componentNames.length; i++)
          components[i] = Component.forName(componentNames[i]);

        generator.generateComponentInterface(components, response.getOutputStream());
      }
      else
      {
        Matcher m = pathPattern.matcher(request.getPathInfo());
        if (m.matches()) {
          String path = m.group(1);
          String resource = m.group(2);

          if (RESOURCE_PATH.equals(path)) {
            writeResource(resource, response.getOutputStream());
            if ("remote.js".equals(resource))
            {
              response.getOutputStream().write("\nSeamRemote.contextPath = \"".getBytes());
              response.getOutputStream().write(request.getContextPath().getBytes());
              response.getOutputStream().write("\";".getBytes());
              response.getOutputStream().flush();
            }
          }
        }
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      log.error("Error", ex);
    }
    finally
    {
      Lifecycle.setServletRequest(null);
      Lifecycle.setPhaseId(null);
      log.debug("ended request");
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
          "org/jboss/seam/servlet/ajax/" + resourceName);

      byte[] buffer = new byte[1024];
      int read = in.read(buffer);
      while (read != -1)
      {
        out.write(buffer, 0, read);
        read = in.read(buffer);
        out.flush();
      }
    }
  }

  /**
   * Writes a cached response to the output stream
   *
   * @param path String
   * @param out OutputStream
   */
  private void writeCachedResponse(String path, OutputStream out)
      throws IOException
  {
    OutputStreamWriter writer = new OutputStreamWriter(out);
    writer.write(resourceCache.get(path));
    writer.flush();
  }
}
