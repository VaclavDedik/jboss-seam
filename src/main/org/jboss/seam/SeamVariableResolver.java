/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.faces.el.EvaluationException;
import javax.management.MBeanServer;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.annotations.ScopeType;
import org.jboss.seam.deployment.SeamDeployer;
import org.jboss.seam.deployment.SeamModule;

/**
 * 
 * Variable resolving: first the method tries to return an object
 * stored in the hierarchical context. If the object does not exist,
 * it is instanciated, stored in the correct context then returned.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamVariableResolver
{

   private static final Logger log = Logger.getLogger(SeamVariableResolver.class);

   private Map<URL, SeamModule> seamModules;

   public SeamVariableResolver()
   {
      MBeanServer mBeanServer = MBeanServerLocator.locate();
      try
      {
         seamModules = (Map<URL, SeamModule>) mBeanServer.getAttribute(SeamDeployer.OBJECT_NAME, "SeamModules");
      }
      catch (Exception e)
      {
         log.error("Error", e);
      }
   }

   public Object resolveVariable(String name, boolean create) throws EvaluationException
   {
      Object result = null;

      if (Contexts.isEventContextActive())
      {
         result = Contexts.getEventContext().get(name);
      }
      if (result == null && Contexts.isConversationContextActive())
      {
         result = Contexts.getConversationContext().get(name);
      }
      if (result == null && Contexts.isLoginContextActive())
      {
         result = Contexts.getSessionContext().get(name);
      }
      if (result == null && Contexts.isBusinessProcessContextActive())
      {
         result = Contexts.getBusinessProcessContext().get(name);
      }
      if (result == null && Contexts.isApplicationContextActive())
      {
         result = Contexts.getApplicationContext().get(name);
      }

      if (result == null && create)
      {
         result = createVariable(name);
      }
      return result;
   }

   public Object createVariable(String name)
   {
      Object result = null;
      SeamComponent seamComponent = findSeamComponent(name);
      if (seamComponent != null)
      {
         if (seamComponent.isStateless())
         {
            result = Contexts.getStatelessContext().get(name);
         }
         else
         {
            if (seamComponent.isStateful())
            {
               InitialContext ctx;
               try
               {
                  ctx = new InitialContext();
                  result = ctx.lookup(name);
               }
               catch (NamingException e)
               {
                  log.error("Error", e);
               }
            }
            else if (seamComponent.isEntity())
            {
               try
               {
                  result = seamComponent.getBean().newInstance();
               }
               catch (InstantiationException e)
               {
                  log.error("Error", e);
               }
               catch (IllegalAccessException e)
               {
                  log.error("Error", e);
               }
            }
            if (seamComponent.getScope() == ScopeType.APPLICATION)
            {
               Contexts.getApplicationContext().set(name, result);
            }
            else if (seamComponent.getScope() == ScopeType.EVENT)
            {
               Contexts.getEventContext().set(name, result);
            }
            else if (seamComponent.getScope() == ScopeType.CONVERSATION)
            {
               Contexts.getConversationContext().set(name, result);
            }
            else if (seamComponent.getScope() == ScopeType.SESSION)
            {
               Contexts.getSessionContext().set(name, result);
            }
         }
      }

      log.info(name + "=" + result);
      return result;
   }

   private SeamComponent findSeamComponent(String name)
   {
      Iterator it = seamModules.values().iterator();
      while (it.hasNext())
      {
         SeamModule module = (SeamModule) it.next();
         SeamComponent seamComponent = module.getSeamComponents().get(name);
         if (seamComponent != null)
         {
            return seamComponent;
         }
      }
      return null;
   }
}
