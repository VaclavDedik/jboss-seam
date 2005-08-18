package org.jboss.seam.example.registration;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.ejb.SeamInterceptor;

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

/**
 * @author Gavin King
 */
@Stateless
@LocalBinding(jndiBinding = "createUser")
@Interceptor(SeamInterceptor.class)
@Name("createUser")
public class CreateUserBean implements CreateUser
{
   
   @PersistenceContext
   private EntityManager manager;
   
   @In(required=true) 
   //@Valid
   private User user;
   
   @IfInvalid(outcome="retry")
   public String createUser()
   {
      manager.persist(user);
      return "success";
   }
   
}


