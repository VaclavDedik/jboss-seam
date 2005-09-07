/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.test.bpm;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.ejb.SeamInterceptor;

/**
 */
@Stateless
@LocalBinding( jndiBinding = "registrationHandler" )
@Interceptor( SeamInterceptor.class )
@Name( "registrationHandler" )
@Scope( ScopeType.STATELESS )
public class RegistrationHandlerBean implements RegistrationHandler, Serializable
{

   static final long serialVersionUID = 3853242352125553207L;

   @PersistenceContext
   private EntityManager manager;

   @In( "user" )
   private User user;

   @CreateProcess( definition = "UserRegistration" )
   public String register()
   {
      System.out.println( "***************************************************************" );
      System.out.println( "Performing RegistrationHandlerBean.register()..." );
      System.out.println( "***************************************************************" );

      manager.persist( user );
      Contexts.getBusinessProcessContext().set( "username", user.getUsername() );

      System.out.println( "***************************************************************" );
      System.out.println( "Done RegistrationHandlerBean.register()..." );
      System.out.println( "***************************************************************" );

      return "register";
   }
}


