package org.jboss.seam.example.security;

/**
 *
 * @author Shane Bryzak
 */
public interface ProtectedLocal
{
  String foo();
  String modifyCustomer();
  String modifyReadonlyCustomer();
}
