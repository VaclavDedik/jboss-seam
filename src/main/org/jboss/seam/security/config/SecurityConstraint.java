package org.jboss.seam.security.config;

import java.util.Set;
import java.util.HashSet;

/**
 * Represents the &lt;security-constraint&gt; element from the configuration.
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
}
