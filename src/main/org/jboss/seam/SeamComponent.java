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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
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
import org.jbpm.db.JbpmSession;
import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

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
   private Class beanClass;
   
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
      this.beanClass = clazz;

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

   public Class getBeanClass()
   {
      return beanClass;
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
               Object bean = beanClass.newInstance();
               inject(bean);
               return bean;
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
   
   public void inject(Object bean)
   {
      injectMethods(bean);
      injectFields(bean);
   }

   public void injectMethods(Object bean)
   {
      for (Method method : getInjectMethods())
      {
         Inject inject = method.getAnnotation(Inject.class);
         if (inject != null)
         {
            if ( method.getReturnType()==Properties.class) 
            {
               injectProperties(bean, method, inject);
            }
            else if ( method.getReturnType()==ProcessInstance.class)
            {
               injectProcessInstance(bean, method, inject);
            }
            else 
            {
               injectComponent(bean, method, inject);
            }
         }
      }
   }

   private void injectFields(Object bean)
   {
      for (Field field : getInjectFields())
      {
         Inject inject = field.getAnnotation(Inject.class);
         if (inject != null)
         {
            if ( field.getType()==Properties.class) 
            {
               injectProperties(bean, field, inject);
            }
            else if ( field.getType()==ProcessInstance.class)
            {
               injectProcessInstance(bean, field, inject);
            }
            else 
            {
               injectComponent(bean, field, inject);
            }
          }
      }
   }

   private void injectProperties(Object bean, Method method, Inject inject)
   {
      String resource = toName( method, inject.value(), ".properties" );
      inject(bean, method, resource, getProperties(bean, resource));
   }

   private void injectProperties(Object bean, Field field, Inject inject)
   {
      String resource = toName( field, inject.value(), ".properties" );
      inject(bean, field, resource, getProperties(bean, resource));
   }

   private Properties getProperties(Object bean, String resource)
   {
      Properties props = new Properties();
      try
      {
         props.load(bean.getClass().getResourceAsStream(resource));
      } 
      catch (IOException ioe)
      {
         throw new RuntimeException(ioe);
      }
      return props;
   }

   private void injectComponent(Object bean, Method method, Inject inject)
   {
      String name = toName(method, inject.value(), "");
      Object value = new SeamVariableResolver()
            .resolveVariable(name, inject.create());
      inject(bean, method, name, value);
   }

   private void injectComponent(Object bean, Field field, Inject inject)
   {
      String name = toName(field, inject.value(), "");
      Object value = new SeamVariableResolver()
            .resolveVariable(name, inject.create());
      inject(bean, field, name, value);
   }

   private String toName(Method method, String name, String extension)
   {
      if (name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4)
               + extension;
      }
      return name;
   }

   private String toName(Field field, String name, String extension)
   {
      if (name.length() == 0)
      {
         name = field.getName() + extension;
      }
      return name;
   }

   private void inject(Object bean, Method method, String name, Object value)
   {
      try
      {
         if (!method.isAccessible())
         {
            method.setAccessible(true);
         }
         method.invoke( bean, new Object[] { value } );
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name, e);
      }
   }

   private void inject(Object bean, Field field, String name, Object value)
   {
      try
      {
         if (!field.isAccessible()) 
         {
            field.setAccessible(true);
         }
         field.set(bean, value);
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name, e);
      }
   }

   private void injectProcessInstance(Object bean, Field field, Inject inject)
   {
      String name = toName( field, inject.value(), "");
      inject(bean, field, name, getProcessInstance(name));
   }

   private void injectProcessInstance(Object bean, Method method, Inject inject)
   {
      String name = toName( method, inject.value(), "");
      inject(bean, method, name, getProcessInstance(name));
   }

   private ProcessInstance getProcessInstance(String name)
   {
      JbpmSessionFactory jbpmSessionFactory = JbpmSessionFactory
            .buildJbpmSessionFactory();

      JbpmSession jbpmSession = jbpmSessionFactory.openJbpmSession();
      jbpmSession.beginTransaction();
      ProcessInstance processInstance = null;
      try
      {
         ProcessDefinition processDefinition = jbpmSession.getGraphSession()
               .findLatestProcessDefinition(name);
         if (processDefinition != null)
         {
            processInstance = new ProcessInstance(processDefinition);
            jbpmSession.getGraphSession().saveProcessInstance(processInstance);
         } 
         else
         {
            throw new InstantiationException("no process instance found: " + name);
         }
      } 
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      return processInstance;
   }


}
