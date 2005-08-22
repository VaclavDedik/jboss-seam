/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.finders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import javax.faces.el.EvaluationException;
import javax.management.MBeanServer;

import org.jboss.logging.Logger;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.Component;
import org.jboss.seam.ComponentType;
import org.jboss.seam.RequiredException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
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
public class ComponentFinder implements Finder
{

   static final Logger log = Logger.getLogger(ComponentFinder.class);

   private Map<URL, SeamModule> seamModules;

   public ComponentFinder()
   {
      MBeanServer mBeanServer = MBeanServerLocator.locate();
      try
      {
         seamModules = (Map<URL, SeamModule>) mBeanServer.getAttribute(SeamDeployer.OBJECT_NAME, "SeamModules");
      }
      catch (Exception e)
      {
         throw new RuntimeException("could not connect to Seam MBean server");
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
      Component component = getComponent(name);
      if (component == null)
      {
         log.info("seam component not found: " + name);
         return null;
      }
      else
      {
         log.info("instantiating seam component: " + name);
         Object instance = component.instantiate();
         if (component.getType()!=ComponentType.STATELESS_SESSION_BEAN)
         {
            if (component.hasCreateMethod())
            {
               String createMethodName = component.getCreateMethod().getName();
               try 
               {
                  instance.getClass().getMethod(createMethodName).invoke(instance);
               }
               catch (Exception e)
               {
                  throw new IllegalArgumentException(e);
               }
            }
            component.getScope().getContext().set(name, instance);
         }
         return instance;
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

   public Object find(In in, String name, Object bean)
   {
      Object result = getComponentInstance(name, in.create());
      if (result==null && in.required())
      {
         throw new RequiredException("In attribute requires value for component: " + name);
      }
      else
      {
         return result;
      }
   }
   
   public String toName(In in, Method method)
   {
      return toName(in.value(), method);
   }

   public String toName(In in, Field field)
   {
      return toName(in.value(), field);
   }
   
   public String toName(Out out, Method method)
   {
      return toName(out.value(), method);
   }

   public String toName(Out out, Field field)
   {
      return toName(out.value(), field);
   }
   
   public String toName(String name, Method method)
   {
      if (name==null || name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4);
      }
      return name;
   }

   public String toName(String name, Field field)
   {
      if (name==null || name.length() == 0)
      {
         name = field.getName();
      }
      return name;
   }

}
