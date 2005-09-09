/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.naming.InitialContext;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.hibernate.validator.ClassValidator;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.Interceptor;
import org.jboss.seam.interceptors.JavaBeanInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Sorter;
import org.jboss.seam.util.StringArrayPropertyEditor;
import org.jboss.seam.util.Strings;

/**
 * A Seam component is any POJO managed by Seam.
 * A POJO is recognized as a Seam component if it is using the org.jboss.seam.annotations.Name annotation
 * 
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.APPLICATION)
public class Component
{
   public static final String PROPERTIES = "org.jboss.seam.properties";
   
   //static
   {
      PropertyEditorManager.registerEditor(String[].class, StringArrayPropertyEditor.class);
   }
   
   private static final Logger log = Logger.getLogger(Component.class);

   private ComponentType type;
   private String name;
   private ScopeType scope;
   private Class<?> beanClass;
   private String jndiName;
   private InterceptionType interceptionType;
   private boolean startup;
   private String[] dependencies;
   
   private Method destroyMethod;
   private Method createMethod;
   private Method unwrapMethod;
   private Set<Method> removeMethods = new HashSet<Method>();
   private Set<Method> validateMethods = new HashSet<Method>();
   private Set<Method> inMethods = new HashSet<Method>();
   private Set<Field> inFields = new HashSet<Field>();
   private Set<Method> outMethods = new HashSet<Method>();
   private Set<Field> outFields = new HashSet<Field>();
   private Map<Method, Object> initializers = new HashMap<Method, Object>();
   
   private ClassValidator validator;
   
   private List<Interceptor> interceptors = new ArrayList<Interceptor>();
   
   private Set<Class> localInterfaces;
   
   private String ifNoConversationOutcome;
   
   private Class<Factory> factory;

   public Component(Class<?> clazz)
   {
      this( clazz, Seam.getComponentName(clazz) );
   }
   
   public Component(Class<?> clazz, String componentName)
   {
      beanClass = clazz;
      name = componentName;
      scope = Seam.getComponentScope(beanClass);
      type = Seam.getComponentType(beanClass);
      interceptionType = Seam.getInterceptionType(beanClass);
      startup = beanClass.isAnnotationPresent(Startup.class);
      if (startup)
      {
         dependencies = getBeanClass().getAnnotation(Startup.class).depends();
      }
      
      log.info("Component: " + getName() + ", scope: " + getScope() + ", type: " + getType());

      if ( beanClass.isAnnotationPresent(Conversational.class) )
      {
         ifNoConversationOutcome = beanClass.getAnnotation(Conversational.class).ifNotBegunOutcome();
      }

      initMembers(clazz);
         
      validator = new ClassValidator(beanClass);
      
      localInterfaces = getLocalInterfaces(beanClass);
      
      jndiName = getJndiName();
      
      initInterceptors();
      
      //TODO: YEW!!!!!
      if ( Contexts.isApplicationContextActive() ) 
      {
         initInitializers();
      }
      
      if (type==ComponentType.JAVA_BEAN)
      {
         factory = createProxyFactory();
      }
      
   }

   private String getJndiName()
   {
      if ( beanClass.isAnnotationPresent(JndiName.class) )
      {
         return beanClass.getAnnotation(JndiName.class).value();
      }
      else
      {
         switch (type) {
            case ENTITY_BEAN:
            case JAVA_BEAN:
               return null;
            default:
               if ( localInterfaces.size()>1 ) {
                  throw new IllegalArgumentException("session beans with multiple business interfaces must specify @JndiName");
               }
               else {
                  return localInterfaces.iterator().next().getName();
               }
         }
      }
   }

   private void initInitializers()
   {
      Map<String, String> properties = (Map) Contexts.getApplicationContext().get(PROPERTIES);
      if (properties==null) return; //TODO: yew!!!!!
      for (Map.Entry<String, String> me: properties.entrySet())
      {
         String key = me.getKey();
         String value = me.getValue();
          
         if ( key.startsWith(name) && key.charAt( name.length() )=='.' )
         {
            String propertyName = key.substring( name.length()+1, key.length() );
            PropertyDescriptor propertyDescriptor;
            try
            {
               propertyDescriptor = new PropertyDescriptor(propertyName, beanClass);
            }
            catch (IntrospectionException ie)
            {
               throw new IllegalArgumentException(ie);
            }
            PropertyEditor propertyEditor = PropertyEditorManager.findEditor( propertyDescriptor.getPropertyType() );
            propertyEditor.setAsText( value );
            initializers.put( propertyDescriptor.getWriteMethod(), propertyEditor.getValue() );
            log.info( key + "=" + value );
        }
         
      }
   }

   private void initMembers(Class<?> clazz)
   {
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
            if ( method.isAnnotationPresent(Unwrap.class) )
            {
               unwrapMethod = method;
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
            if ( !field.isAccessible() )
            {
               field.setAccessible(true);
            }
         }
         
      }
   }

   private void initInterceptors()
   {
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
      
      log.trace("interceptor stack: " + interceptors);
   }

   private void initDefaultInterceptors()
   {
      interceptors.add( new Interceptor( new OutcomeInterceptor(), this ) );
      interceptors.add( new Interceptor( new RemoveInterceptor(), this ) );
      interceptors.add( new Interceptor( new BusinessProcessInterceptor(), this ) );
      interceptors.add( new Interceptor( new ConversationInterceptor(), this ) );
      interceptors.add( new Interceptor( new BijectionInterceptor(), this ) );
      interceptors.add( new Interceptor( new ValidationInterceptor(), this ) );
   }

   public Class<?> getBeanClass()
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

   public boolean hasUnwrapMethod() 
   {
      return unwrapMethod!=null;
   }

   public Method getUnwrapMethod()
   {
      return unwrapMethod;
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

   public Object newInstance()
   {
      try 
      {
         return initialize( instantiate() );
      }
      catch (Exception e)
      {
         throw new InstantiationException("Could not instantiate component", e);
      }
   }

    protected Object instantiate() throws Exception
    {
        switch(type) {
           case JAVA_BEAN: 
              if (interceptionType==InterceptionType.NEVER)
              {
                 return beanClass.newInstance();
              }
              else
              {
                 Factory bean = factory.newInstance();
                 bean.setCallback( 0, new JavaBeanInterceptor() );
                 return bean;
              }
           case ENTITY_BEAN:
              return beanClass.newInstance();
           case STATELESS_SESSION_BEAN : 
           case STATEFUL_SESSION_BEAN :
              return new InitialContext().lookup(jndiName);
           default:
              throw new IllegalStateException();
        }
    }
   
   protected Object initialize(Object bean) throws Exception
   {
      for (Map.Entry<Method, Object> me: initializers.entrySet())
      {
         Reflections.invoke( me.getKey(), bean, me.getValue() );
      }
      return bean;
   }
   
   public void inject(Object bean/*, boolean isActionInvocation*/)
   {
      injectMethods(bean/*, isActionInvocation*/);
      injectFields(bean/*, isActionInvocation*/);
   }

   public void outject(Object bean)
   {
      outjectMethods(bean);
      outjectFields(bean);
   }

   private void injectMethods(Object bean/*, boolean isActionInvocation*/)
   {
      for (Method method : getInMethods())
      {
         In in = method.getAnnotation(In.class);
         //if ( isActionInvocation || in.alwaysDefined() )
         //{
            String name = toName(in.value(), method);
            inject( bean, method, name, getInstanceToInject(in, name, bean) );
         //}
      }
   }

   private void injectFields(Object bean/*, boolean isActionInvocation*/)
   {
      for (Field field : getInFields())
      {
         In in = field.getAnnotation(In.class);
         //if ( isActionInvocation || in.alwaysDefined() )
         //{
            String name = toName(in.value(), field);
            inject( bean, field, name, getInstanceToInject(in, name, bean) );
         //}
      }
   }

   private void outjectFields(Object bean)
   {
      for (Field field : getOutFields())
      {
         Out out = field.getAnnotation(Out.class);
         if (out != null)
         {
            setOutjectedValue( out, toName(out.value(), field), outject(bean, field) );
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
            setOutjectedValue( out, toName(out.value(), method), outject(bean, method) );
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
         Component component = Component.forName(name);
         if (value!=null && component!=null)
         {
            if ( !component.isInstance(value) )
            {
               throw new IllegalArgumentException("attempted to bind an Out attribute of the wrong type to: " + name);
            }
         }
         ScopeType scope = component==null ? 
               ScopeType.CONVERSATION : component.getScope();
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
            Class clazz = bean.getClass();
            for (Class intfc: localInterfaces)
            {
               if (intfc.isAssignableFrom(clazz))
               {
                  return true;
               }
            }
            return false;
      }
   }
   
   private static Set<Class> getLocalInterfaces(Class clazz)
   {
       Set<Class> result = new HashSet<Class>();

       if (clazz.isAnnotationPresent(Local.class)) {
           Local local = (Local) clazz.getAnnotation(Local.class);
           for (Class iface: local.value()) {
               result.add(iface);
           }
       } else {
           for (Class iface: clazz.getInterfaces()) {
               if (iface.isAnnotationPresent(Local.class)) {
                   result.add(iface);
               }
           }

           if (result.size() == 0) {
               for (Class iface: clazz.getInterfaces()) {
                   if (!isExcludedLocalInterfaceName(iface.getName())) {
                       result.add(iface);
                   }
               }
           }
       }

       return result;
   }


    private static boolean isExcludedLocalInterfaceName(String name) {
        return name.equals("java.io.Serializable") ||
               name.equals("java.io.Externalizable") ||
               name.startsWith("javax.ejb.");
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
         return Reflections.invoke(method, bean);
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
         Reflections.invoke(method, bean, value );
      } 
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not inject: " + name + " to: " + getName(), e);
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
         throw new IllegalArgumentException("could not inject: " + name + " to: " + getName(), e);
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
   
   public static Component forName(String name)
   {
      return (Component) getInstance( name + ".component", false );
   }

   public static Object getInstance(Class clazz, boolean create)
   {
      return getInstance( Seam.getComponentName(clazz), create );
   }

   public static Object getInstance(String name, boolean create)
   {
      Object result = Contexts.lookupInStatefulContexts(name);
      if (result == null && create)
      {
          result = newInstance(name);
      }
      if (result!=null) 
      {
         Component component = Component.forName(name);
         if (component!=null)
         {
            if ( !component.isInstance(result) )
            {
               throw new IllegalArgumentException("value found for In attribute has the wrong type: " + name);
            }
         }
         result = unwrap( component, result );
         if ( log.isTraceEnabled() ) 
         {
            log.trace( Strings.toString(result) );
         }
      }
      return result;
   }

   public static Object newInstance(String name)
   {
       
      Component component = Component.forName(name);
      if (component == null)
      {
         log.info("seam component not found: " + name);
         return null; //needed when this method is called by JSF
      }
      else
      {
         log.info("instantiating seam component: " + name);
         Object instance = component.newInstance();
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
         String createMethodName = createMethod.getName();
         try 
         {
            Method method = instance.getClass().getMethod(createMethodName, paramTypes);
            if ( paramTypes.length==0 )
            {
               Reflections.invokeAndWrap( method, instance );
            }
            else {
               Reflections.invokeAndWrap( method, instance, component );
            }
         }
         catch (NoSuchMethodException e)
         {
            throw new IllegalArgumentException("create method not found", e);
         }
      }
   }

   private static Object unwrap(Component component, Object instance)
   {
      if (component!=null && component.hasUnwrapMethod())
      {
         instance = Reflections.invokeAndWrap(component.getUnwrapMethod(), instance);
      }
      return instance;
   }

   private static Object getInstanceToInject(In in, String name, Object bean)
   {
      Object result = getInstance(name, in.create());
      if (result==null && in.required())
      {
         throw new RequiredException("In attribute requires value for component: " + name);
      }
      else
      {
         return result;
      }
   }
   
   private static String toName(String name, Method method)
   {
      if (name==null || name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4);
      }
      return name;
   }

   private static String toName(String name, Field field)
   {
      if (name==null || name.length() == 0)
      {
         name = field.getName();
      }
      return name;
   }
   
   public String toString()
   {
      return "Component(" + name + ")";
   }
   
   private Class<Factory> createProxyFactory()
   {
      Enhancer en = new Enhancer();
      en.setUseCache(false);
      en.setInterceptDuringConstruction(false);
      en.setCallbackType(MethodInterceptor.class);
      en.setSuperclass(beanClass);
      return en.createClass();
   }

   public InterceptionType getInterceptionType()
   {
      return interceptionType;
   }

	public boolean isStartup() {
		return startup;
	}

   public String[] getDependencies()
   {
      return dependencies;
   }
   
}
