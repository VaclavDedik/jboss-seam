//$Id$
package org.jboss.seam.example.jbms;

import javax.ejb.Local;

@Local
public interface Register
{
   public String register();
}