package org.jboss.seam.security.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the &lt;security-constraint&gt; element from the security
 * configuration file.
 *
 * @author Shane Bryzak
 */
public class SecurityConstraint
{
  private Set<WebResourceCollection> resourceCollections = new HashSet<WebResourceCollection>();
  private AuthConstraint authConstraint;

  public Set<WebResourceCollection> getResourceCollections()
  {
    return resourceCollections;
  }

  public void setResourceCollections(Set<WebResourceCollection> resourceCollections)
  {
    this.resourceCollections = resourceCollections;
  }

  public AuthConstraint getAuthConstraint()
  {
    return authConstraint;
  }

  public void setAuthConstraint(AuthConstraint authConstraint)
  {
    this.authConstraint = authConstraint;
  }

  /**
   * Check if the specified URI and method are included in this security constraint.
   *
   * @param uri String The URI to check
   * @param method String The method to check
   * @return boolean True if the URI and method match one of the patterns contained
   * within this security constraint, false otherwise.
   */
  public boolean included(String uri, String method)
  {
    if (method == null)
      return false;

    for (WebResourceCollection c : resourceCollections)
    {
      if (!c.supportsMethod(method))
        continue;

      for (String pattern : c.getUrlPatterns())
      {
        if (matchPattern(uri, pattern))
          return true;
      }
    }

    return false;
  }

  /**
   * Pattern matching code, adapted from Tomcat. This method checks to see if
   * the specified path matches the specified pattern.
   *
   * @param path String The path to check
   * @param pattern String The pattern to check the path against
   * @return boolean True if the path matches the pattern, false otherwise
   */
  private boolean matchPattern(String path, String pattern)
  {
    if (path == null || "".equals(path))
      path = "/";
    if (pattern == null || "".equals(pattern))
      pattern = "/";

    // Check for an exact match
    if (path.equals(pattern))
      return true;

    // Check for path prefix matching
    if (pattern.startsWith("/") && pattern.endsWith("/*"))
    {
      pattern = pattern.substring(0, pattern.length() - 2);
      if (pattern.length() == 0)
        return true;

      if (path.endsWith("/"))
        path = path.substring(0, path.length() - 1);

      while (true)
      {
        if (pattern.equals(path))
          return true;
        int slash = path.lastIndexOf('/');
        if (slash <= 0)
          break;
        path = path.substring(0, slash);
      }
      return false;
    }

    // Check for suffix matching
    if (pattern.startsWith("*."))
    {
      int slash = path.lastIndexOf('/');
      int period = path.lastIndexOf('.');
      if ( (slash >= 0) && (period > slash) &&
          path.endsWith(pattern.substring(1)))
      {
        return true;
      }
      return false;
    }

    // Check for universal mapping
    if (pattern.equals("/"))
      return true;

    return false;
  }
}
