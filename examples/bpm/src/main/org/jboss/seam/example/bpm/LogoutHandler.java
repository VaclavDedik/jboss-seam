//$Id$
package org.jboss.seam.example.bpm;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateless
@Name( "logout" )
@Interceptor( SeamInterceptor.class )
public class LogoutHandler implements Logout
{

   @In
   private Context sessionContext;

   public String logout()
   {
      Seam.invalidateSession();
      sessionContext.remove( "loggedIn" );
      return "home";
   }
}
