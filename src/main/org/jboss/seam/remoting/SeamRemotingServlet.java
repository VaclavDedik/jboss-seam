package org.jboss.seam.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.jboss.seam.servlet.SeamServletFilter;

/**
 * Provides remoting capabilities for Seam.
 *
 * @author Shane Bryzak
 */
public class SeamRemotingServlet extends HttpServlet
{
  private static final Log log = LogFactory.getLog(SeamServletFilter.class);

  private static final Pattern pathPattern = Pattern.compile("/(.*?)/([^/]+)");

  private static final String RESOURCE_PATH = "resource";

  private ServletContext servletContext;

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
      RequestHandler handler = RequestHandlerFactory.getInstance().getRequestHandler(request.getPathInfo());
      if (handler != null)
      {
        handler.setServletContext(servletContext);
        handler.handle(request, response);
      }
      else
      {
        Matcher m = pathPattern.matcher(request.getPathInfo());
        if (m.matches()) {
          String path = m.group(1);
          String resource = m.group(2);

          if (RESOURCE_PATH.equals(path)) {
            writeResource(resource, response.getOutputStream());
            if ("remote.js".equals(resource)) {
              response.getOutputStream().write("\nSeam.Remoting.contextPath = \"".
                                               getBytes());
              response.getOutputStream().write(request.getContextPath().
                                               getBytes());
              response.getOutputStream().write("\";".getBytes());
              response.getOutputStream().flush();
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
        while (read != -1) {
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
