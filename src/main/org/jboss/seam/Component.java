/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;

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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
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
 * @author Gavin King
 * @version $Revision$
 */
public class Component
{
   private ComponentType type;
   private String name;
   private ScopeType scope;
   private Class beanClass;
   
   private SeamModule seamModule;
   
   private Method destroyMethod;
   private Method createMethod;
   private Set<Method> removeMethods = new HashSet<Method>();
   private Set<Method> inMethods = new HashSet<Method>();
   private Set<Field> inFields = new HashSet<Field>();
   private Set<Method> outMethods = new HashSet<Method>();
   private Set<Field> outFields = new HashSet<Field>();

   public Set<Field> getOutFields()
   {
      return outFields;
   }

   public Set<Method> getOutMethods()
   {
      return outMethods;
   }

   public Component(SeamModule seamModule, Class<?> clazz)
   {
      this.seamModule = seamModule;
      this.name = Seam.getComponentName(clazz);
      this.beanClass = clazz;

      scope = Seam.getComponentScope(clazz);

      if ( clazz.isAnnotationPresent(Stateful.class) )
      {
         type = STATEFUL_SESSION_BEAN;
         seamModule.getEJB3Beans().put( 
               clazz.getAnnotation(Stateful.class).name(), 
               clazz.getCanonicalName()
            );
      }

      else if ( clazz.isAnnotationPresent(Stateless.class) )
      {
         type = STATELESS_SESSION_BEAN;
         seamModule.getEJB3Beans().put(
               clazz.getAnnotation(Stateless.class).name(), 
               clazz.getCanonicalName()
            );
      }

      else if ( clazz.isAnnotationPresent(Entity.class) )
      {
         type = ENTITY_BEAN;
         seamModule.getEJB3Beans().put(
               clazz.getAnnotation(Entity.class).name(), 
               clazz.getCanonicalName()
            );
      }
      
      else {
         type = JAVA_BEAN;
       }

      
      for (Method method: clazz.getDeclaredMethods()) //TODO: inheritance!
      {
         if ( method.isAnnotationPresent(Remove.class) )
         {
            removeMethods.add(method);  
         }
         if ( method.isAnnotationPresent(Destroy.class) )
         {
            destroyMethod = method;
         }
         if ( method.isAnnotationPresent(Create.class) )
         {
            createMethod = method;
         }
         if ( method.isAnnotationPresent(In.class) )
         {
            inMethods.add(method);
         }
         if ( method.isAnnotationPresent(Out.class) )
         {
            outMethods.add(method);
         }
      }
      
      for (Field field: clazz.getDeclaredFields()) //TODO: inheritance!
      {
         if ( field.isAnnotationPresent(In.class) )
         {
            inFields.add(field);
         }
         if ( field.isAnnotationPresent(Out.class) )
         {
            outFields.add(field);
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
   
   public ComponentType getType()
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

   public Set<Method> getInMethods()
   {
      return inMethods;
   }

   public Set<Field> getInFields()
   {
      return inFields;
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

   public void outject(Object bean)
   {
      outjectMethods(bean);
      outjectFields(bean);
   }

   public void injectMethods(Object bean)
   {
      for (Method method : getInMethods())
      {
         In inject = method.getAnnotation(In.class);
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
      for (Field field : getInFields())
      {
         In inject = field.getAnnotation(In.class);
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

   public void outjectFields(Object bean)
   {
      for (Field field : getOutFields())
      {
         Out out = field.getAnnotation(Out.class);
         if (out != null)
         {
            outjectComponent(bean, field, out);
         }
      }
   }

   private void outjectMethods(Object bean)
   {
      for (Method method : getOutMethods())
      {
         Out out = method.getAnnotation(Out.class);
         if (out != null)
         {
            outjectComponent(bean, method, out);
         }
      }
   }

   private void injectProperties(Object bean, Method method, In inject)
   {
      String resource = toName( method, inject.value(), ".properties" );
      inject(bean, method, resource, getProperties(bean, resource));
   }

   private void injectProperties(Object bean, Field field, In inject)
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

   private Object getInjectedValue(In inject, String name)
   {
      return new Finder()
            .getComponentInstance(name, inject.create());
   }

   private void injectComponent(Object bean, Method method, In inject)
   {
      String name = toName(method, inject.value(), "");
      inject( bean, method, name, getInjectedValue(inject, name) );
   }

   private void injectComponent(Object bean, Field field, In inject)
   {
      String name = toName(field, inject.value(), "");
      inject( bean, field, name, getInjectedValue(inject, name) );
   }

   private void setOutjectedValue(String name, Object value)
   {
      new Finder().getComponent(name)
            .getScope().getContext().set(name, value);
   }

   private void outjectComponent(Object bean, Method method, Out out)
   {
      setOutjectedValue( toName(method, out.value(), ""), outject(bean, method) );
   }

   private void outjectComponent(Object bean, Field field, Out out)
   {
      setOutjectedValue( toName(field, out.value(), ""), outject(bean, field) );
   }

   private Object outject(Object bean, Field field)
   {
      try {
         return field.get(bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not outject: " + name, e);         
      }
   }

   private Object outject(Object bean, Method method)
   {
      try {
         return method.invoke(bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not outject: " + name, e);         
      }
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

   private void injectProcessInstance(Object bean, Field field, In inject)
   {
      String name = toName( field, inject.value(), "");
      inject(bean, field, name, getProcessInstance(name));
   }

   private void injectProcessInstance(Object bean, Method method, In inject)
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
