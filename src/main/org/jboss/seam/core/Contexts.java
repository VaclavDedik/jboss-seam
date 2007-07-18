/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;

/**
 * Provides access to the current contexts associated with the thread.
 * 
 * @author Gavin King
 */
@Name("org.jboss.seam.core.contexts")
@BypassInterceptors
@Install(precedence=BUILT_IN)
@Scope(ScopeType.APPLICATION)
public class Contexts 
{

   @Factory("org.jboss.seam.core.eventContext")
   public Context getEventContext() 
   {
      return org.jboss.seam.contexts.Contexts.getEventContext();
   }

   @Factory("org.jboss.seam.core.methodContext")
   public Context getMethodContext() 
   {
      return org.jboss.seam.contexts.Contexts.getMethodContext();
   }

   @Factory("org.jboss.seam.core.pageContext")
   public Context getPageContext() 
   {
      return org.jboss.seam.contexts.Contexts.getPageContext();
   }

   @Factory("org.jboss.seam.core.sessionContext")
   public Context getSessionContext() 
   {
      return org.jboss.seam.contexts.Contexts.getSessionContext();
   }

   @Factory("org.jboss.seam.core.applicationContext")
   public Context getApplicationContext() 
   {
      return org.jboss.seam.contexts.Contexts.getApplicationContext();
   }

   @Factory("org.jboss.seam.core.conversationContext")
   public Context getConversationContext() 
   {
      return org.jboss.seam.contexts.Contexts.getConversationContext();
   }

   @Factory("org.jboss.seam.core.businessProcessContext")
   public Context getBusinessProcessContext() 
   {
      return org.jboss.seam.contexts.Contexts.getBusinessProcessContext();
   }

}
