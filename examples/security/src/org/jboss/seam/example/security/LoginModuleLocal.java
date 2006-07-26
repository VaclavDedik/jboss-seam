package org.jboss.seam.example.security;

/**
 *
 * @author Shane Bryzak
 */
public interface LoginModuleLocal
{
  void login(String username, String password) throws SecurityException;
  String getPrincipal();
  String[] getRoles();
  void destroy();
}
