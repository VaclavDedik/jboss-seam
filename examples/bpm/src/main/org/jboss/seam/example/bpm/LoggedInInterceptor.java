//$Id$
package org.jboss.seam.example.bpm;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jbpm.security.Authentication;

// before (or outside of) these interceptors
@Around( { BijectionInterceptor.class, ValidationInterceptor.class, ConversationInterceptor.class } )
// and after (or inside of) these interceptors
@Within( RemoveInterceptor.class )
public class LoggedInInterceptor
{
   private static final Logger log = Logger.getLogger( LoggedInInterceptor.class );

   // currently a very simple impl which always logs the 'admin' user in

   @AroundInvoke
   public Object checkLoggedIn(InvocationContext invocation) throws Exception
   {
      User user = ( User ) Contexts.getSessionContext().get( "user" );
      if ( user == null )
      {
         // in the eventual real impl, we'd want to redirect them to login
         user = new User();
         user.setName( "Administrator" );
         user.setUsername( "admin" );
         user.setPassword( "admin" );
         Contexts.getSessionContext().set( "user", user );
      }

      Authentication.pushAuthenticatedActorId( user.getUsername() );
      try
      {
         return invocation.proceed();
      }
      finally
      {
         Authentication.popAuthenticatedActorId();
      }
   }

}
