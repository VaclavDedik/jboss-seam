package org.jboss.seam.example.security;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.seam.annotations.Name;

/**
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("protectedAction")
@SecurityDomain("seam")
public class ProtectedAction implements ProtectedLocal
{
  @RolesAllowed("admin")
  public String foo()
  {
    System.out.println("protected method foo() successfully called");
    return "protected";
  }
}
