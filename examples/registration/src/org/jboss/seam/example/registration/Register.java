//$Id$
package org.jboss.seam.example.registration;

import javax.ejb.Local;
import javax.faces.context.FacesContext;

@Local
public interface Register
{
   public void logClientIP(FacesContext facesContext);

   public String register();
}