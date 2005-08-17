package org.jboss.seam.example.bpm;

import java.io.Serializable;

import javax.ejb.Interceptor;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.BusinessProcessContext;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ejb.SeamInterceptor;
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
@Stateless
@LocalBinding(jndiBinding = "userManagement")
@Interceptor(SeamInterceptor.class)
@Name("userManagement")
@Scope(ScopeType.STATELESS)
public class UserManagementBean implements UserManagement, Serializable
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 1L;
   
   @PersistenceContext
   private EntityManager manager;
   
   @In("user")
   private User user;
   
   @In(value="MyProcessDefinition", create=true)
   private ProcessInstance processInstance;
   
   
   public String register()
   {
      manager.persist(user);
      BusinessProcessContext bpc = (BusinessProcessContext)Contexts.getBusinessProcessContext();
      bpc.signal("submit for approval");
      /*
      JbpmSessionFactory jbpmSessionFactory = BusinessProcessContext.jbpmSessionFactory;
      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      */
      return "register";
   }
}


