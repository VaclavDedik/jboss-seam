package org.jboss.seam.example.seamspace;

import javax.ejb.Local;

/**
 * Local interface for loginAction
 *
 * @author Shane Bryzak
 */
@Local
public interface LoginLocal
{
  String login();
  String logout();
}
