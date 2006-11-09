package org.jboss.seam.example.security;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Secure;
import org.jboss.seam.annotations.security.Permission;
import org.jboss.seam.security.SeamSecurityManager;

/**
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("protectedAction")
@Secure(roles = "admin")
public class ProtectedAction implements ProtectedLocal
{
  @Secure(permissions = {@Permission(name = "protected", action = "call")})
  public String foo()
  {
    System.out.println("protected method foo() successfully called");
    return "protected";
  }

  public String modifyCustomer()
  {
    Customer customer = new Customer();

    SeamSecurityManager.instance().checkPermission(customer, "modify");

    return "modified";
  }

  public String modifyReadonlyCustomer()
  {
    Customer customer = new Customer();
    customer.setReadonly(true);

    SeamSecurityManager.instance().checkPermission(customer, "modify");

    return "modified";
  }
}
