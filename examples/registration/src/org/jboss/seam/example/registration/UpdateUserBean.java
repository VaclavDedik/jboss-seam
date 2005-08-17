package org.jboss.seam.example.registration;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.annotations.BeginConversationIf;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.EndConversation;
import org.jboss.seam.annotations.Name;
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
@Stateful
@LocalBinding(jndiBinding = "updateUser")
@Interceptor(SeamInterceptor.class)
@Name("updateUser")
public class UpdateUserBean implements UpdateUser, Serializable
{
   
   private static final Log log = LogFactory.getLog(UpdateUser.class);
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager manager;
   
   private User user;
   
   private String username;
   
   @TransactionAttribute(NOT_SUPPORTED)
   public String getUsername() {
      return username;
   }
   
   @TransactionAttribute(NOT_SUPPORTED)
   public void setUsername(String name) {
      username = name;
   }
   
   @BeginConversationIf(result="success")
   public String findUser() {
      log.info("finding User");
      user = manager.find(User.class, username);
      return user==null ? "not-found" : "success";
   }
   
   @TransactionAttribute(NOT_SUPPORTED)
   public User getUser() {
      return user;
   }
   
   @EndConversation
   public String updateUser()
   {
      log.info("updating User");
      return "success";
   }
   
   @Destroy 
   @Remove
   public void destroy() {}
   
}


