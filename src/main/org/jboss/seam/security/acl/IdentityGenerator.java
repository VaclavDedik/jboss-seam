package org.jboss.seam.security.acl;

import java.io.Serializable;

/**
 * Identity generator
 *
 * @author Shane Bryzak
 */
public interface IdentityGenerator
{
  String generateIdentity(Object obj);
}
