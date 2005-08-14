package org.jboss.seam.example.registration;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.Contexts;
import org.jboss.seam.SeamInterceptor;
import org.jboss.seam.annotations.BeginConversation;
import org.jboss.seam.annotations.EndConversation;
import org.jboss.seam.annotations.Inject;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
@Stateless
@LocalBinding(jndiBinding = "userManagement")
@Interceptor(SeamInterceptor.class)
@Name("userManagement")
@Scope(ScopeType.STATELESS)
public class UserManagementBean implements UserManagement, Serializable
{
   
   @PersistenceContext
   private EntityManager manager;
   
   @Inject(create=false)
   private User user;
   
   private String username = "";
   
   public String register()
   {
      manager.persist(user);
      return "success";
   }
   
   @BeginConversation
   public String retrieve() {
      user = (User) manager.createQuery("from User where username = :username")
            .setParameter("username", username)
            .getSingleResult();
      Contexts.getConversationContext().set("user", user);
      return "success";
   }
   
   @EndConversation
   public String setPassword()
   {
      manager.merge(user);
      return "success";
   }
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String userName)
   {
      this.username = userName;
   }
}


