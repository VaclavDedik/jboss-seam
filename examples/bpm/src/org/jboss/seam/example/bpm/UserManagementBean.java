package org.jboss.seam.example.bpm;
import javax.ejb.Interceptor;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.seam.SeamInterceptor;
import org.jboss.seam.annotations.Inject;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;
import org.jbpm.graph.exe.ProcessInstance;

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
@Stateful
@Remote(UserManagement.class)
@RemoteBinding(jndiBinding = "userManagement")
@Interceptor(SeamInterceptor.class)
@Name("userManagement")
@Scope(ScopeType.APPLICATION)
public class UserManagementBean implements UserManagement, java.io.Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   
   @PersistenceContext
   private EntityManager manager;
   
   @Inject("user")
   private User user;
   
   @Inject("MyProcessDefinition")
   private ProcessInstance processInstance;
   
   public String register()
   {
      manager.persist(user);
      return "register";
   }
}


