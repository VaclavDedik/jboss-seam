/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.finders;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.el.EvaluationException;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ComponentType;
import org.jboss.seam.RequiredException;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.components.Components;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Strings;

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

   private static final Logger log = Logger.getLogger(ComponentFinder.class);

   public static Object getComponentInstance(String name, boolean create) throws EvaluationException
   {
      Object result = Contexts.lookupInStatefulContexts(name);
      if (result == null && create)
      {
          result = newComponentInstance(name);
      }
      if (result!=null) 
      {
         Component component = Seam.getComponent(name);
         if (component!=null)
         {
            if ( !component.isInstance(result) )
            {
               throw new IllegalArgumentException("value found for In attribute has the wrong type: " + name);
            }
         }
         result = unwrap( component, result );
         log.info( Strings.toString(result) );
      }
      return result;
   }

   public static Object newComponentInstance(String name)
   {
      Component component = getComponent(name);
      if (component == null)
      {
         log.info("seam component not found: " + name);
         return null; //needed when this method is called by JSF
      }
      else
      {
         log.info("instantiating seam component: " + name);
         Object instance = component.instantiate();
         if (component.getType()!=ComponentType.STATELESS_SESSION_BEAN)
         {
            callCreateMethod(component, instance);
            component.getScope().getContext().set(name, instance);
         }
         return instance;
      }
   }

   private static void callCreateMethod(Component component, Object instance)
   {
      if (component.hasCreateMethod())
      {
         Method createMethod = component.getCreateMethod();
         Class[] paramTypes = createMethod.getParameterTypes();
         Object param = paramTypes.length==0 ? null : component;
         String createMethodName = createMethod.getName();
         try 
         {
            instance.getClass().getMethod(createMethodName, paramTypes)
                  .invoke(instance, param);
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException(e);
         }
      }
   }

   private static Object unwrap(Component component, Object instance)
   {
      if (component!=null && component.hasUnwrapMethod())
      {
         try 
         {
            instance = component.getUnwrapMethod().invoke(instance);
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException(e);
         }
      }
      return instance;
   }

   public static Component getComponent(String name)
   {
      return ( (Components) Contexts.getApplicationContext().get(Components.class) ).getComponent(name);
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
