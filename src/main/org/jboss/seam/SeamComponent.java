/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import static org.jboss.seam.SeamComponentType.ENTITY_BEAN;
import static org.jboss.seam.SeamComponentType.JAVA_BEAN;
import static org.jboss.seam.SeamComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.SeamComponentType.STATELESS_SESSION_BEAN;
import static org.jboss.seam.annotations.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.ScopeType.EVENT;
import static org.jboss.seam.annotations.ScopeType.STATELESS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.persistence.Entity;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Inject;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.ScopeType;
import org.jboss.seam.deployment.SeamModule;

/**
 * A Seam component is any POJO managed by Seam.
 * A POJO is recognized as a Seam component if it is using the org.jboss.seam.annotations.Name annotation
 * 
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamComponent
{
   private SeamComponentType type;
   private String name;
   private ScopeType scope;
   private Class bean;
   
   private SeamModule seamModule;
   
   private Method destroyMethod;
   private Method createMethod;
   private Set<Method> removeMethods = new HashSet<Method>();
   private Set<Method> injectMethods = new HashSet<Method>();
   private Set<Field> injectFields = new HashSet<Field>();

   public SeamComponent(SeamModule seamModule, Class<?> clazz)
   {
      this.seamModule = seamModule;
      this.name = Seam.getComponentName(clazz);
      this.bean = clazz;

      // Set up the component scope
      boolean hasScopeAnnotation = clazz.isAnnotationPresent(Scope.class);
      if (hasScopeAnnotation)
      {
         scope = Seam.getComponentScope(clazz);
      }

      if ( clazz.isAnnotationPresent(Stateful.class) )
      {
         type = STATEFUL_SESSION_BEAN;
         if (!hasScopeAnnotation) 
         {
            scope = CONVERSATION;
         }
         seamModule.getEJB3Beans().put( 
               clazz.getAnnotation(Stateful.class).name(), 
               clazz.getCanonicalName()
            );
      }

      else if ( clazz.isAnnotationPresent(Stateless.class) )
      {
         type = STATELESS_SESSION_BEAN;
         if (!hasScopeAnnotation) 
         {
            scope = STATELESS;
         }
         seamModule.getEJB3Beans().put(
               clazz.getAnnotation(Stateless.class).name(), 
               clazz.getCanonicalName()
            );
      }

      else if ( clazz.isAnnotationPresent(Entity.class) )
      {
         type = ENTITY_BEAN;
         if (!hasScopeAnnotation) 
         {
            scope = CONVERSATION;
         }
         seamModule.getEJB3Beans().put(
               clazz.getAnnotation(Entity.class).name(), 
               clazz.getCanonicalName()
            );
      }
      
      else {
         type = JAVA_BEAN;
         if (!hasScopeAnnotation) 
         {
            scope = EVENT;
         }
      }

      
      for (Method method: clazz.getDeclaredMethods()) //TODO: inheritance!
      {
         if ( method.isAnnotationPresent(Destroy.class) )
         {
            destroyMethod = method;
         }
         if ( method.isAnnotationPresent(Remove.class) )
         {
            removeMethods.add(method);  
         }
         if ( method.isAnnotationPresent(Create.class) )
         {
            createMethod = method;
         }
         if ( method.isAnnotationPresent(Inject.class) )
         {
            injectMethods.add(method);
         }
      }
      
      for (Field field: clazz.getDeclaredFields()) //TODO: inheritance!
      {
         if ( field.isAnnotationPresent(Inject.class) )
         {
            injectFields.add(field);
         }
      }
   }

   public Class getBean()
   {
      return bean;
   }

   public String getName()
   {
      return name;
   }
   
   public SeamComponentType getType()
   {
      return type;
   }


   public SeamModule getSeamModule()
   {
      return seamModule;
   }

   public ScopeType getScope()
   {
      return scope;
   }
   
   public Method getDestroyMethod()
   {
      return destroyMethod;
   }

   public Set<Method> getRemoveMethods()
   {
      return removeMethods;
   }
   
   public boolean hasDestroyMethod() 
   {
      return destroyMethod!=null;
   }

   public boolean hasCreateMethod() 
   {
      return createMethod!=null;
   }

   public Method getCreateMethod()
   {
      return createMethod;
   }

   public Set<Method> getInjectMethods()
   {
      return injectMethods;
   }

   public Set<Field> getInjectFields()
   {
      return injectFields;
   }

   public Object instantiate()
   {
      try 
      {
         switch(type)
         {
            case JAVA_BEAN: 
            case ENTITY_BEAN:
               return bean.newInstance();
            case STATELESS_SESSION_BEAN : 
            case STATEFUL_SESSION_BEAN :
               return new InitialContext().lookup(name);
            default:
               throw new IllegalStateException();
         }
      }
      catch (Exception e)
      {
         throw new InstantiationException("Could not instantiate component", e);
      }
   }

}
