package org.jboss.seam.example.security;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Secure;

/**
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("protectedAction")
@Secure(roles = "admin")
public class ProtectedAction implements ProtectedLocal
{
  public String foo()
  {
    System.out.println("protected method foo() successfully called");
    return "protected";
  }
}
