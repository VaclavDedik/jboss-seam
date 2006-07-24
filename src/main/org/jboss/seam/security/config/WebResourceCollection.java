package org.jboss.seam.security.config;

import java.util.Set;
import java.util.HashSet;

/**
 * Represents the <web-resource-collection> element in the config file.
 *
 * @author Shane Bryzak
 */
public class WebResourceCollection
{
  private Set<String> urlPatterns = new HashSet<String>();
  private Set<String> httpMethods = new HashSet<String>();

  public Set<String> getUrlPatterns()
  {
    return urlPatterns;
  }

  public void setUrlPatterns(Set<String> urlPatterns)
  {
    this.urlPatterns = urlPatterns;
  }

  public Set<String> getHttpMethods()
  {
    return httpMethods;
  }

  public void setHttpMethods(Set<String> httpMethods)
  {
    this.httpMethods = httpMethods;
  }
}
