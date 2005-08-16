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
import org.jboss.seam.deployment.SeamDeployer;
import org.jboss.seam.deployment.SeamModule;
import org.jboss.seam.util.Tool;

/**
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

   static final Logger log = Logger.getLogger(SeamVariableResolver.class);

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
      Object result = Contexts.lookupInStatefulContexts(name);
      if (result == null && create)
      {
          result = createVariable(name);
      }
      if (result!=null) 
      {
         log.info( Tool.toString(result) );
      }
      return result;
   }

   private Object createVariable(String name)
   {
      log.info("instantiating: " + name);
      Object result = null;
      SeamComponent seamComponent = findSeamComponent(name);
      if (seamComponent != null)
      {
         if (seamComponent.isStateless())
         {
            result = instantiateSession(name, result);
         }
         else
         {
            if (seamComponent.isStateful())
            {
               result = instantiateSession(name, result);
            }
            else if (seamComponent.isEntity())
            {
               result = instantiateEntity(result, seamComponent);
            }
            if (seamComponent.hasCreateMethod())
            {
               try 
               {
                  result.getClass().getMethod(seamComponent.getCreateMethod().getName()).invoke(result);
               }
               catch (Exception e)
               {
                  throw new RuntimeException(e);
               }
            }
            seamComponent.getScope().getContext().set(name, result);
         }
      }
      return result;
   }

   private Object instantiateSession(String name, Object result)
   {
      try
      {
         return new InitialContext().lookup(name);
      }
      catch (NamingException e)
      {
         log.error("Error", e);
         throw new InstantiationException("Could not find component in JNDI", e);
      }
   }

   private Object instantiateEntity(Object result, SeamComponent seamComponent)
   {
      try
      {
         return seamComponent.getBean().newInstance();
      }
      catch (java.lang.InstantiationException e)
      {
         log.error("Error", e);
         throw new InstantiationException("Could not instantiate component", e);
      }
      catch (IllegalAccessException e)
      {
         log.error("Error", e);
         throw new InstantiationException("Could not instantiate component", e);
      }
   }

   public SeamComponent findSeamComponent(String name)
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
