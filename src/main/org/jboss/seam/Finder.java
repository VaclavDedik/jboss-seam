/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.net.URL;
import java.util.Map;

import javax.faces.el.EvaluationException;
import javax.management.MBeanServer;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.contexts.Contexts;
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
public class Finder
{

   static final Logger log = Logger.getLogger(Finder.class);

   private Map<URL, SeamModule> seamModules;

   public Finder()
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

   public Object getComponentInstance(String name, boolean create) throws EvaluationException
   {
      Object result = Contexts.lookupInStatefulContexts(name);
      if (result == null && create)
      {
          result = newComponentInstance(name);
      }
      if (result!=null) 
      {
         log.info( Tool.toString(result) );
      }
      return result;
   }

   public Object newComponentInstance(String name)
   {
      Component seamComponent = getComponent(name);
      if (seamComponent == null)
      {
         log.info("seam component not found: " + name);
         return null;
      }
      else
      {
         log.info("instantiating seam component: " + name);
         Object result = seamComponent.instantiate();
         if (seamComponent.getType()!=ComponentType.STATELESS_SESSION_BEAN)
         {
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
         return result;
      }
   }

   public Component getComponent(String name)
   {
      for (SeamModule module: seamModules.values())
      {
         Component seamComponent = module.getSeamComponents().get(name);
         if (seamComponent != null)
         {
            return seamComponent;
         }
      }
      return null;
   }
}
