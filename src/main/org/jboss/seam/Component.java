/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.naming.InitialContext;

import org.hibernate.validator.ClassValidator;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.deployment.SeamModule;
import org.jboss.seam.finders.ComponentFinder;
import org.jboss.seam.finders.Finder;
import org.jboss.seam.finders.Finders;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.Interceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.util.Sorter;

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
   private static final Logger log = Logger.getLogger(Component.class);

   private ComponentType type;
   private String name;
   private ScopeType scope;
   private Class<?> beanClass;
   
   private SeamModule seamModule;
   
   private Method destroyMethod;
   private Method createMethod;
   private Set<Method> removeMethods = new HashSet<Method>();
   private Set<Method> validateMethods = new HashSet<Method>();
   private Set<Method> inMethods = new HashSet<Method>();
   private Set<Field> inFields = new HashSet<Field>();
   private Set<Method> outMethods = new HashSet<Method>();
   private Set<Field> outFields = new HashSet<Field>();
   private Set<Field> validateFields = new HashSet<Field>();
   
   private ClassValidator validator;
   
   private List<Interceptor> interceptors = new ArrayList<Interceptor>();

   private ComponentFinder finder;
   
   private Set<Class> localInterfaces;
   
   private String ifNoConversationOutcome;

   public Component(SeamModule seamModule, Class<?> clazz)
   {
      this.seamModule = seamModule;
      this.beanClass = clazz;
      name = Seam.getComponentName(beanClass);
      scope = Seam.getComponentScope(beanClass);
      type = Seam.getComponentType(beanClass);
      
      log.info("Component: " + getName() + ", scope: " + getScope() + ", type: " + getType());

      if ( beanClass.isAnnotationPresent(Conversational.class) )
      {
         ifNoConversationOutcome = beanClass.getAnnotation(Conversational.class).ifNotBegunOutcome();
      }

      for (;clazz!=Object.class; clazz = clazz.getSuperclass())
      {
      
         for (Method method: clazz.getDeclaredMethods()) //TODO: inheritance!
         {
            if ( method.isAnnotationPresent(IfInvalid.class) )
            {
               validateMethods.add(method);  
            }
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
            if ( !method.isAccessible() )
            {
               method.setAccessible(true);
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
            if ( field.isAnnotationPresent(IfInvalid.class) )
            {
               validateFields.add(field);
            }
            if ( !field.isAccessible() )
            {
               field.setAccessible(true);
            }
         }
         
      }
         
      validator = new ClassValidator(beanClass);
      
      localInterfaces = getLocalInterfaces(beanClass);
      
      initDefaultInterceptors();
      
      for (Annotation annotation: beanClass.getAnnotations())
      {
         if ( annotation.annotationType().isAnnotationPresent(javax.ejb.Interceptor.class) )
         {
            interceptors.add( new Interceptor(annotation, this) );
         }
      }
      
      new Sorter<Interceptor>() {
         protected boolean isOrderViolated(Interceptor outside, Interceptor inside)
         {
            Class<?> insideClass = inside.getUserInterceptor().getClass();
            Class<?> outsideClass = outside.getUserInterceptor().getClass();
            Around around = insideClass.getAnnotation(Around.class);
            Within within = outsideClass.getAnnotation(Within.class);
            return ( around!=null && Arrays.asList( around.value() ).contains( outsideClass ) ) ||
                  ( within!=null && Arrays.asList( within.value() ).contains( insideClass ) );
         }
      }.sort(interceptors);
      
      log.info("interceptor stack: " + interceptors);
      
      finder = new ComponentFinder();
      
   }

   private void initDefaultInterceptors()
   {
      interceptors.add( new Interceptor( new OutcomeInterceptor(), this ) );
      interceptors.add( new Interceptor( new RemoveInterceptor(), this ) );
      interceptors.add( new Interceptor( new ConversationInterceptor(), this ) );
      interceptors.add( new Interceptor( new BijectionInterceptor(), this ) );
      interceptors.add( new Interceptor( new ValidationInterceptor(), this ) );
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
   
   public ClassValidator getValidator() 
   {
      return validator;
   }
   
   public List<Interceptor> getInterceptors()
   {
      return interceptors;
   }
   
   public Method getDestroyMethod()
   {
      return destroyMethod;
   }

   public Set<Method> getRemoveMethods()
   {
      return removeMethods;
   }
   
   public Set<Method> getValidateMethods()
   {
      return validateMethods;
   }
   
   public Set<Field> getValidateFields()
   {
      return validateFields;
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

   public Set<Field> getOutFields()
   {
      return outFields;
   }

   public Set<Method> getOutMethods()
   {
      return outMethods;
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
         In in = method.getAnnotation(In.class);
         if (in != null)
         {
            Finder finder = Finders.getFinder(method.getReturnType());
            String name = finder.toName( in, method );
            inject( bean, method, name, finder.find(in, name, bean) );
         }
      }
   }

   private void injectFields(Object bean)
   {
      for (Field field : getInFields())
      {
         In in = field.getAnnotation(In.class);
         if (in != null)
         {
            Finder finder = Finders.getFinder(field.getType());
            String name = finder.toName( in, field );
            inject( bean, field, name, finder.find(in, name, bean) );
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
            setOutjectedValue( out, finder.toName(out, field), outject(bean, field) );
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
            setOutjectedValue( out, finder.toName(out, method), outject(bean, method) );
         }
      }
   }

   private void setOutjectedValue(Out out, String name, Object value)
   {
      if (value==null && out.required())
      {
         throw new RequiredException("Out attribute requires value for component: " + name);
      }
      else 
      {
         Component component = finder.getComponent(name);
         if (value!=null && component!=null)
         {
            if ( !component.isInstance(value) )
            {
               throw new IllegalArgumentException("attempted to bind an Out attribute of the wrong type to: " + name);
            }
         }
         ScopeType scope = component==null ? ScopeType.CONVERSATION : component.getScope();
         scope.getContext().set(name, value);
      }
   }
   
   public boolean isInstance(Object bean)
   {
      switch(type)
      {
         case JAVA_BEAN:
         case ENTITY_BEAN:
            return beanClass.isInstance(bean);
         default:
            return localInterfaces.contains(bean.getClass());
      }
   }
   
   public static Set<Class> getLocalInterfaces(Class clazz)
   {
      Set<Class> result = new HashSet<Class>();
      for (Class iface: clazz.getInterfaces())
      {
         if ( iface.isAnnotationPresent(Local.class))
         {
            result.add(iface);
         }
      }
      return result;
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

   private void inject(Object bean, Method method, String name, Object value)
   {  
      try
      {
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
         field.set(bean, value);
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name, e);
      }
   }
   
   public boolean isConversational()
   {
      return ifNoConversationOutcome!=null;
   }
   
   public String getNoConversationOutcome()
   {
      return ifNoConversationOutcome;
   }

}
