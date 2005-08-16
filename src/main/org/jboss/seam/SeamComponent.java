/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Remove;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
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
//   private boolean managedBean = false;
   
   private boolean stateless = false;

   private boolean stateful = false;

   private boolean entity = false;

   private String name;
   
   private ScopeType scope;
   
   private Class bean;

   private SeamModule seamModule;
   
   private Method destroyMethod;
   private Method createMethod;
   
   private Set<Method> removeMethods = new HashSet<Method>();

   public SeamComponent(SeamModule seamModule, Class clazz)
   {
      this.seamModule = seamModule;  
      this.bean = clazz;
      //TODO: init *all* state here in the constructor
      for (Method method: clazz.getMethods())
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

   public void setName(String name)
   {
      this.name = name;
   }

   public boolean isStateless()
   {
      return stateless;
   }

   public void setStateless(boolean stateless)
   {
      this.stateless = stateless;
   }

   public boolean isEntity()
   {
      return entity;
   }

   public void setEntity(boolean entity)
   {
      this.entity = entity;
   }

   public boolean isStateful()
   {
      return stateful;
   }

   public void setStateful(boolean stateful)
   {
      this.stateful = stateful;
   }

   public SeamModule getSeamModule()
   {
      return seamModule;
   }

   public ScopeType getScope()
   {
      return scope;
   }

   public void setScope(ScopeType scope)
   {
      this.scope = scope;
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

   public void setCreateMethod(Method createMethod)
   {
      this.createMethod = createMethod;
   }

}
