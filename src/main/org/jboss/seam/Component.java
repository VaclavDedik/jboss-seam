/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import static org.jboss.seam.ComponentType.ENTITY_BEAN;
import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.ComponentType.MESSAGE_DRIVEN_BEAN;
import static org.jboss.seam.ComponentType.STATEFUL_SESSION_BEAN;
import static org.jboss.seam.ComponentType.STATELESS_SESSION_BEAN;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpSessionActivationListener;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.DataBinderClass;
import org.jboss.seam.annotations.DataSelectorClass;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Mutable;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.databinding.DataBinder;
import org.jboss.seam.databinding.DataSelector;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ClientSideInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.ConversationalInterceptor;
import org.jboss.seam.interceptors.ExceptionInterceptor;
import org.jboss.seam.interceptors.Interceptor;
import org.jboss.seam.interceptors.JavaBeanInterceptor;
import org.jboss.seam.interceptors.ManagedEntityIdentityInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.RollbackInterceptor;
import org.jboss.seam.interceptors.TransactionInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.util.Conversions;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Parameters;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.SortItem;
import org.jboss.seam.util.SorterNew;
import org.jboss.seam.util.Conversions.PropertyValue;

/**
 * A Seam component is any POJO managed by Seam.
 * A POJO is recognized as a Seam component if it has a @Name annotation
 *
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.APPLICATION)
public class Component
{
   public static final String PROPERTIES = "org.jboss.seam.properties";

   private static final Log log = LogFactory.getLog(Component.class);

   private ComponentType type;
   private String name;
   private ScopeType scope;
   private Class<?> beanClass;
   private String jndiName;
   private InterceptionType interceptionType;
   private boolean startup;
   private String[] dependencies;
   private boolean mutable;

   private Method destroyMethod;
   private Method createMethod;
   private Method unwrapMethod;
   private Method preDestroyMethod;
   private Method postConstructMethod;
   private Method prePassivateMethod;
   private Method postActivateMethod;
   private Set<Method> removeMethods = new HashSet<Method>();
   private Set<Method> validateMethods = new HashSet<Method>();
   private Set<Method> inMethods = new HashSet<Method>();
   private Set<Field> inFields = new HashSet<Field>();
   private Set<Method> outMethods = new HashSet<Method>();
   private Set<Field> outFields = new HashSet<Field>();
   private Set<Field> parameterFields = new HashSet<Field>();
   private Set<Method> parameterSetters = new HashSet<Method>();
   private Map<Method, InitialValue> initializerSetters = new HashMap<Method, InitialValue>();
   private Map<Field, InitialValue> initializerFields = new HashMap<Field, InitialValue>();

   private List<Method> dataModelGetters = new ArrayList<Method>();
   private Map<Method, Annotation> dataModelGetterAnnotations = new HashMap<Method, Annotation>();
   private Map<String, Method> dataModelSelectionSetters = new HashMap<String, Method>();
   private Map<Method, Annotation> dataModelSelectionSetterAnnotations = new HashMap<Method, Annotation>();
   private List<Field> dataModelFields = new ArrayList<Field>();
   private Map<Field, Annotation> dataModelFieldAnnotations = new HashMap<Field, Annotation>();
   private Map<String, Field> dataModelSelectionFields = new HashMap<String, Field>();
   private Map<Field, Annotation> dataModelSelectionFieldAnnotations = new HashMap<Field, Annotation>();

   private Field logField;
   private org.jboss.seam.log.Log logInstance;

   private Hashtable<Locale, ClassValidator> validators = new Hashtable<Locale, ClassValidator>();

   private List<Interceptor> interceptors = new ArrayList<Interceptor>();
   private List<Interceptor> clientSideInterceptors = new ArrayList<Interceptor>();

   private Set<Class> businessInterfaces;

   private Class<Factory> factory;

   //only used for tests
   public Component(Class<?> clazz)
   {
      this( clazz, Seam.getComponentName(clazz) );
   }

   // only used for tests
   public Component(Class<?> clazz, String componentName)
   {
      this(clazz, componentName, Seam.getComponentScope(clazz));
   }

   // only used for tests
   public Component(Class<?> clazz, Context applicationContext)
   {
      this( clazz, Seam.getComponentName(clazz), Seam.getComponentScope(clazz), applicationContext );
   }

   public Component(Class<?> clazz, String componentName, ScopeType componentScope)
   {
      this(clazz, componentName, componentScope, Contexts.getApplicationContext());
   }

   private Component(Class<?> clazz, String componentName, ScopeType componentScope, Context applicationContext)
   {
      beanClass = clazz;
      name = componentName;
      scope = componentScope;
      type = Seam.getComponentType(beanClass);
      interceptionType = Seam.getInterceptionType(beanClass);

      checkScopeForComponentType();

      startup = beanClass.isAnnotationPresent(Startup.class);
      if (startup)
      {
         if (scope!=SESSION && scope!=APPLICATION)
         {
            throw new IllegalArgumentException("@Startup only supported for SESSION or APPLICATION scoped components: " + name);
         }
         dependencies = getBeanClass().getAnnotation(Startup.class).depends();
      }

      mutable = beanClass.isAnnotationPresent(Mutable.class);
      if (mutable)
      {
         Init.instance().getMutableComponentNames().add(name);
      }

      jndiName = getJndiName(applicationContext);

      log.info(
            "Component: " + getName() +
            ", scope: " + getScope() +
            ", type: " + getType() +
            ", class: " + beanClass.getName() +
            ( jndiName==null ? "" : ", JNDI: " + jndiName )
         );

      initMembers(clazz, applicationContext);
      checkDestroyMethod();

      businessInterfaces = getBusinessInterfaces(beanClass);

      if ( interceptionType!=InterceptionType.NEVER)
      {
         initInterceptors();
      }

      initInitializers(applicationContext);

      /*if (type==JAVA_BEAN)
      {*/
         factory = createProxyFactory();
      //}

   }

   private void checkScopeForComponentType()
   {
      if ( scope==STATELESS && (type==STATEFUL_SESSION_BEAN || type==ENTITY_BEAN) )
      {
         throw new IllegalArgumentException("Only stateless session beans and Java beans may be bound to the STATELESS context: " + name);
      }
      if ( scope==PAGE && type==STATEFUL_SESSION_BEAN )
      {
         throw new IllegalArgumentException("Stateful session beans may not be bound to the PAGE context: " + name);
      }
      if ( scope==APPLICATION && type==STATEFUL_SESSION_BEAN )
      {
         log.warn("Stateful session beans was bound to the APPLICATION context - note that it is not safe to make concurrent calls to the bean: " + name);
      }
      if ( scope!=STATELESS && type==MESSAGE_DRIVEN_BEAN )
      {
         throw new IllegalArgumentException("Message-driven beans must be bound to STATELESS context: " + name);
      }

      boolean serializableScope = scope==PAGE || scope==SESSION || scope==CONVERSATION;
      boolean serializableType = type==JAVA_BEAN || type==ENTITY_BEAN;
      if ( serializableType && serializableScope && !Serializable.class.isAssignableFrom(beanClass) )
      {
         log.warn("Component class should be serializable: " + name);
      }
   }

   private void checkDestroyMethod()
   {
      if ( type==STATEFUL_SESSION_BEAN && ( destroyMethod==null || !removeMethods.contains(destroyMethod) ) )
      {
         throw new IllegalArgumentException("Stateful session bean component should have a method marked @Remove @Destroy: " + name);
      }
   }

   private String getJndiName(Context applicationContext)
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
               if (applicationContext==null) return null; //TODO: Yew!!!
               String jndiPattern = Init.instance().getJndiPattern();
               if (jndiPattern==null)
               {
                  throw new IllegalArgumentException("You must specify org.jboss.seam.core.init.jndiPattern or use @JndiName: " + name);
               }
               return jndiPattern.replace( "#{ejbName}", Seam.getEjbName(beanClass) );
         }
      }
   }

   private void initInitializers(Context applicationContext)
   {
      if (applicationContext==null) return; //TODO: yew!!!!!
      Map<String, Conversions.PropertyValue> properties = (Map<String, Conversions.PropertyValue>) applicationContext.get(PROPERTIES);
      if (properties==null) return; //TODO: yew!!!!!


      for ( Map.Entry<String, Conversions.PropertyValue> me: properties.entrySet() )
      {
         String key = me.getKey();
         Conversions.PropertyValue propertyValue = me.getValue();

         if ( key.startsWith(name) && key.charAt( name.length() )=='.' )
         {
            if ( log.isDebugEnabled() ) log.debug( key + "=" + propertyValue );

            if ( type==ENTITY_BEAN )
            {
               throw new IllegalArgumentException("can not configure entity beans: " + name);
            }

            String propertyName = key.substring( name.length()+1, key.length() );
            Method setterMethod = Reflections.getSetterMethod(beanClass, propertyName);
            if (setterMethod!=null)
            {
               if ( !setterMethod.isAccessible() ) setterMethod.setAccessible(true);
               Class parameterClass = setterMethod.getParameterTypes()[0];
               Type parameterType = setterMethod.getGenericParameterTypes()[0];
               initializerSetters.put( setterMethod, getInitialValue(propertyValue, parameterClass, parameterType) );
            }
            else
            {
               try
               {
                  Field field = beanClass.getField(propertyName);
                  if ( !field.isAccessible() ) field.setAccessible(true);
                  initializerFields.put( field, getInitialValue(propertyValue, field.getType(), field.getGenericType()) );
               }
               catch (NoSuchFieldException nsfe)
               {
                  throw new IllegalArgumentException("no field or setter method for configuration setting: " + key, nsfe);
               }
            }
        }

      }
   }

   private InitialValue getInitialValue(Conversions.PropertyValue propertyValue, Class parameterClass, Type parameterType)
   {
      //note that org.jboss.seam.core.init.jndiPattern looks like an EL expression but is not one!
      if ( propertyValue.isExpression() && !beanClass.equals(Init.class) ) //TODO: support #{...} in <value> element
      {
         return new ELInitialValue(propertyValue, parameterClass, parameterType);
      }
      else
      {
         return new ConstantInitialValue(propertyValue, parameterClass, parameterType);
      }
   }


   private void initMembers(Class<?> clazz, Context applicationContext)
   {
      List<Method> selectionSetters = new ArrayList<Method>();
      List<Field> selectionFields = new ArrayList<Field>();

      for (;clazz!=Object.class; clazz = clazz.getSuperclass())
      {

         for (Method method: clazz.getDeclaredMethods())
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
               if (type!=JAVA_BEAN && type!=STATEFUL_SESSION_BEAN)
               {
                  throw new IllegalArgumentException("Only JavaBeans and stateful session beans support @Destroy methods: " + name);
               }
               if (destroyMethod!=null)
               {
                  throw new IllegalStateException("component has two @Destroy methods: " + name);
               }
               destroyMethod = method;
            }
            if ( method.isAnnotationPresent(Create.class) )
            {
               if (type!=JAVA_BEAN && type!=STATEFUL_SESSION_BEAN)
               {
                  throw new IllegalArgumentException("Only JavaBeans and stateful session beans support @Create methods: " + name);
               }
               if (createMethod!=null)
               {
                  throw new IllegalStateException("component has two @Create methods: " + name);
               }
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
               if (unwrapMethod!=null)
               {
                  throw new IllegalStateException("component has two @Unwrap methods: " + name);
               }
               unwrapMethod = method;
            }
            if ( method.isAnnotationPresent(DataModel.class) ) //TODO: generalize
            {
               checkDataModelScope( method.getAnnotation(DataModel.class) );
            }
            if ( method.isAnnotationPresent(org.jboss.seam.annotations.Factory.class) )
            {
               Init init = (Init) applicationContext.get( Seam.getComponentName(Init.class) ); //can't use Init.instance() here 'cos of unit tests
               String contextVariable = toName( method.getAnnotation(org.jboss.seam.annotations.Factory.class).value(), method );
               init.addFactoryMethod(contextVariable, method, this);
            }
            if ( method.isAnnotationPresent(Observer.class) )
            {
               Init init = (Init) applicationContext.get( Seam.getComponentName(Init.class) ); //can't use Init.instance() here 'cos of unit tests
               for ( String eventType : method.getAnnotation(Observer.class).value() )
               {
                  if ( eventType.length()==0 ) eventType = method.getName();
                  init.addObserverMethod(eventType, method, this);
               }
            }
            if ( method.isAnnotationPresent(RequestParameter.class) )
            {
               parameterSetters.add(method);
            }
            if ( method.isAnnotationPresent(PrePassivate.class) )
            {
               prePassivateMethod = method;
            }
            if ( method.isAnnotationPresent(PostActivate.class) )
            {
               postActivateMethod = method;
            }
            if ( method.isAnnotationPresent(PostConstruct.class) )
            {
               postConstructMethod = method;
            }
            if ( method.isAnnotationPresent(PreDestroy.class) )
            {
               preDestroyMethod = method;
            }

            for ( Annotation ann: method.getAnnotations() )
            {
               if ( ann.annotationType().isAnnotationPresent(DataBinderClass.class) )
               {
                  dataModelGetters.add(method);
                  dataModelGetterAnnotations.put(method, ann);
               }
               if ( ann.annotationType().isAnnotationPresent(DataSelectorClass.class) )
               {
                  selectionSetters.add(method);
                  dataModelSelectionSetterAnnotations.put(method, ann);
               }
            }

            if ( !method.isAccessible() )
            {
               method.setAccessible(true);
            }

         }

         for (Field field: clazz.getDeclaredFields())
         {
            if ( field.isAnnotationPresent(In.class) )
            {
               inFields.add(field);
            }
            if ( field.isAnnotationPresent(Out.class) )
            {
               outFields.add(field);
            }
            if ( field.isAnnotationPresent(DataModel.class) ) //TODO: generalize
            {
               checkDataModelScope( field.getAnnotation(DataModel.class) );
            }
            if ( field.isAnnotationPresent(RequestParameter.class) )
            {
               parameterFields.add(field);
            }
            if ( field.isAnnotationPresent(org.jboss.seam.annotations.Logger.class) )
            {
               logField=field;
               String category = field.getAnnotation(org.jboss.seam.annotations.Logger.class).value();
               if ( "".equals( category ) )
               {
                  logInstance = new org.jboss.seam.log.LogImpl(beanClass);
               }
               else
               {
                  logInstance = new org.jboss.seam.log.LogImpl(category);
               }
            }
            for ( Annotation ann: field.getAnnotations() )
            {
               if ( ann.annotationType().isAnnotationPresent(DataBinderClass.class) )
               {
                  dataModelFields.add(field);
                  dataModelFieldAnnotations.put(field, ann);
               }
               if ( ann.annotationType().isAnnotationPresent(DataSelectorClass.class) )
               {
                  selectionFields.add(field);
                  dataModelSelectionFieldAnnotations.put(field, ann);
               }
            }

            if ( !field.isAccessible() )
            {
               field.setAccessible(true);
            }

         }

      }

      final boolean hasMultipleDataModels = dataModelGetters.size() + dataModelFields.size() > 1;
      String defaultDataModelName = null;
      if ( !hasMultipleDataModels )
      {
         if ( !dataModelGetters.isEmpty() )
         {
            Method dataModelGetter = dataModelGetters.get(0);
            Annotation ann = dataModelGetterAnnotations.get(dataModelGetter);
            String name = createWrapper(ann).getVariableName(ann);
            defaultDataModelName = toName( name, dataModelGetter );
         }
         else if ( !dataModelFields.isEmpty() )
         {
            Field dataModelField = dataModelFields.get(0);
            Annotation ann = dataModelFieldAnnotations.get(dataModelField);
            String name = createWrapper(ann).getVariableName(ann);
            defaultDataModelName = toName( name, dataModelField );
         }
      }

      for (Method method: selectionSetters)
      {
         Annotation ann = dataModelSelectionSetterAnnotations.get(method);
         String name = createUnwrapper(ann).getVariableName(ann);
         if ( name.length() == 0 )
         {
            if ( hasMultipleDataModels )
            {
               throw new IllegalStateException( "Missing value() for @DataModelSelection with multiple @DataModels" );
            }
            name = defaultDataModelName;
         }
         Method existing = dataModelSelectionSetters.put( name, method );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelection setters for: " + name);
         }
      }

      for (Field field: selectionFields)
      {
         Annotation ann = dataModelSelectionFieldAnnotations.get(field);
         String name = createUnwrapper(ann).getVariableName(ann);
         if ( name.length() == 0 )
         {
            if ( hasMultipleDataModels )
            {
               throw new IllegalStateException( "Missing value() for @DataModelSelection with multiple @DataModels" );
            }
            name = defaultDataModelName;
         }
         Field existing = dataModelSelectionFields.put( name, field );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelection fields for: " + name);
         }
      }

   }

   private void checkDataModelScope(DataModel dataModel) {
      ScopeType dataModelScope = dataModel.scope();
      if ( dataModelScope!=PAGE && dataModelScope!=UNSPECIFIED )
      {
         throw new IllegalArgumentException("@DataModel scope must be ScopeType.UNSPECIFIED or ScopeType.PAGE: " + name);
      }
   }

   private void initInterceptors()
   {
      initDefaultInterceptors();

      for (Annotation annotation: beanClass.getAnnotations())
      {
         if ( annotation.annotationType().isAnnotationPresent(Interceptors.class) )
         {
            addInterceptor( new Interceptor(annotation, this) );
         }
      }

      newSort(interceptors);

      if ( log.isDebugEnabled() ) log.debug("interceptor stack: " + interceptors);
   }

   public void addInterceptor(Interceptor interceptor)
   {
      if ( interceptor.getType()==InterceptorType.SERVER)
      {
         interceptors.add(interceptor);
      }
      else
      {
         clientSideInterceptors.add(interceptor);
      }
   }

   private List<Interceptor> newSort(List<Interceptor> list)
   {
      List<SortItem<Interceptor>> siList = new ArrayList<SortItem<Interceptor>>();
      Map<Class<?>,SortItem<Interceptor>> ht = new HashMap<Class<?>,SortItem<Interceptor>>();

      for (Interceptor i : list)
      {
         SortItem<Interceptor> si = new SortItem<Interceptor>(i);
         siList.add(si);
         ht.put( i.getUserInterceptorClass(), si );
      }

      for (SortItem<Interceptor> si : siList)
      {
         Class<?> clazz = si.getObj().getUserInterceptorClass();
         if ( clazz.isAnnotationPresent(org.jboss.seam.annotations.Interceptor.class) )
         {
            org.jboss.seam.annotations.Interceptor interceptorAnn = clazz.getAnnotation(org.jboss.seam.annotations.Interceptor.class);
            for (Class<?> cl : Arrays.asList( interceptorAnn.around() ) )
            {
               si.getAround().add( ht.get(cl) );
            }
            for (Class<?> cl : Arrays.asList( interceptorAnn.within() ) )
            {
               si.getWithin().add( ht.get(cl) );
            }
         }
      }

      SorterNew<Interceptor> sList = new SorterNew<Interceptor>();
      siList = sList.sort(siList);

      list.clear();
      for (SortItem<Interceptor> si : siList)
      {
         list.add( si.getObj() );
      }
      return list ;
   }

   private void initDefaultInterceptors()
   {
      addInterceptor( new Interceptor( new ExceptionInterceptor(), this ) );
      addInterceptor( new Interceptor( new RemoveInterceptor(), this ) );
      addInterceptor( new Interceptor( new ConversationalInterceptor(), this ) );
      addInterceptor( new Interceptor( new BusinessProcessInterceptor(), this ) );
      addInterceptor( new Interceptor( new ConversationInterceptor(), this ) );
      addInterceptor( new Interceptor( new OutcomeInterceptor(), this ) );
      addInterceptor( new Interceptor( new BijectionInterceptor(), this ) );
      addInterceptor( new Interceptor( new ValidationInterceptor(), this ) );
      addInterceptor( new Interceptor( new RollbackInterceptor(), this ) );
      if ( getType()==JAVA_BEAN )
      {
         addInterceptor( new Interceptor( new TransactionInterceptor(), this ) );
      }
      if ( getType()!=STATELESS_SESSION_BEAN )
      {
         addInterceptor( new Interceptor( new ManagedEntityIdentityInterceptor(), this ) );
      }
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
      java.util.ResourceBundle bundle = Contexts.isApplicationContextActive() ? //yew, just for testing!
            ResourceBundle.instance() : null;
      Locale locale = bundle==null ?
            new Locale("DUMMY") : bundle.getLocale();
      ClassValidator validator = validators.get(locale);
      if (validator==null)
      {
         validator = bundle==null ?
               new ClassValidator(beanClass) :
               new ClassValidator(beanClass, bundle);
         validators.put(locale, validator);
      }
      return validator;
   }

   public List<Interceptor> getInterceptors(InterceptorType type)
   {
      switch(type)
      {
         case SERVER: return interceptors;
         case CLIENT: return clientSideInterceptors;
         case ANY:
            List<Interceptor> all = new ArrayList<Interceptor>();
            all.addAll(clientSideInterceptors);
            all.addAll(interceptors);
            return all;
         default: throw new IllegalArgumentException("no interceptor type specified");
      }
   }

   public List<Object> createUserInterceptors(InterceptorType type)
   {
      List<Interceptor> interceptors = getInterceptors(type);
      List<Object> result = new ArrayList<Object>( interceptors.size() );
      for (Interceptor interceptor: interceptors)
      {
         result.add( interceptor.createUserInterceptor() );
      }
      return result;
   }

   /**
    * For use with Seam debug page.
    *
    * @return the server-side interceptor stack
    */
   public List<Interceptor> getServerSideInterceptors()
   {
      return getInterceptors(InterceptorType.SERVER);
   }

   /**
    * For use with Seam debug page.
    *
    * @return the client-side interceptor stack
    */
   public List<Interceptor> getClientSideInterceptors()
   {
      return getInterceptors(InterceptorType.CLIENT);
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

   public boolean hasPreDestroyMethod()
   {
      return preDestroyMethod!=null;
   }

   public boolean hasPostConstructMethod()
   {
      return postConstructMethod!=null;
   }

   public boolean hasPrePassivateMethod()
   {
      return prePassivateMethod!=null;
   }

   public boolean hasPostActivateMethod()
   {
      return postActivateMethod!=null;
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

    public boolean needsInjection() {
        return !getInFields().isEmpty() ||
            !getInMethods().isEmpty() ||
            !dataModelSelectionSetters.isEmpty() ||
            !dataModelSelectionFields.isEmpty() ||
            !parameterFields.isEmpty() ||
            !parameterSetters.isEmpty() ||
            logField!=null;
    }

    public boolean needsOutjection() {
        return !getOutFields().isEmpty() ||
            !getOutMethods().isEmpty() ||
            !dataModelGetters.isEmpty() ||
            !dataModelFields.isEmpty();
    }

    protected Object instantiate() throws Exception
    {
        switch(type) {
           case JAVA_BEAN:
              if (interceptionType==InterceptionType.NEVER)
              {
                 Object bean = beanClass.newInstance();
                 initialize(bean);
                 callPostConstructMethod(bean);
                 return bean;
              }
              else
              {
                 Factory bean = factory.newInstance();
                 initialize(bean);
                 bean.setCallback( 0, new JavaBeanInterceptor(this) );
                 callPostConstructMethod(bean);
                 return bean;
              }
           case ENTITY_BEAN:
              return beanClass.newInstance();
           case STATELESS_SESSION_BEAN:
           case STATEFUL_SESSION_BEAN:
              return wrap( Naming.getInitialContext().lookup(jndiName) );
           case MESSAGE_DRIVEN_BEAN:
              throw new UnsupportedOperationException("Message-driven beans may not be called: " + name);
           default:
              throw new IllegalStateException();
        }
    }

    private Object wrap(Object bean) throws Exception
    {
       Factory proxy = factory.newInstance();
       proxy.setCallback( 0, new ClientSideInterceptor(bean, this) );
       return proxy;
    }

   public void initialize(Object bean) throws Exception
   {
      if ( log.isDebugEnabled() ) log.debug("initializing new instance of: " + name);

      injectLog(bean);

      for ( Map.Entry<Method, InitialValue> me: initializerSetters.entrySet() )
      {
         Method method = me.getKey();
         Object initialValue = me.getValue().getValue( method.getParameterTypes()[0] );
         setPropertyValue(bean, method, method.getName(), initialValue );
      }
      for ( Map.Entry<Field, InitialValue> me: initializerFields.entrySet() )
      {
         Field field = me.getKey();
         Object initialValue = me.getValue().getValue( field.getType() );
         setFieldValue(bean, field, field.getName(), initialValue );
      }
   }

   /**
    * Inject context variable values into @In attributes
    * of a component instance.
    *
    * @param bean a Seam component instance
    * @param enforceRequired should we enforce required=true?
    */
   public void inject(Object bean, boolean enforceRequired)
   {
      //injectLog(bean);
      injectMethods(bean, enforceRequired);
      injectFields(bean, enforceRequired);
      injectDataModelSelection(bean);
      injectParameters(bean);
   }

   /**
    * Null out any @In attributes of a component instance.
    *
    * @param bean a Seam component instance
    */
   public void disinject(Object bean)
   {
      disinjectMethods(bean);
      disinjectFields(bean);
   }

   private void injectLog(Object bean)
   {
      if (logField!=null)
      {
         setFieldValue(bean, logField, "log", logInstance);
      }
   }

   private void injectParameters(Object bean)
   {
      Map<String, String[]> requestParameters = Parameters.getRequestParameters();

      for (Method setter: parameterSetters)
      {
         String name = toName( setter.getAnnotation(RequestParameter.class).value(), setter );
         Class<?> setterType = setter.getParameterTypes()[0];
         Object convertedValue = Parameters.convertMultiValueRequestParameter(requestParameters, name, setterType);
         setPropertyValue( bean, setter, name, convertedValue );
      }
      for (Field field: parameterFields)
      {
         String name = toName( field.getAnnotation(RequestParameter.class).value(), field );
         Class<?> fieldType = field.getType();
         Object convertedValue = Parameters.convertMultiValueRequestParameter(requestParameters, name, fieldType);
         setFieldValue( bean, field, name, convertedValue );
      }
   }

   /**
    * Outject context variable values from @Out attributes
    * of a component instance.
    *
    * @param bean a Seam component instance
    * @param enforceRequired should we enforce required=true?
    */
   public void outject(Object bean, boolean enforceRequired)
   {
      outjectMethods(bean, enforceRequired);
      outjectFields(bean, enforceRequired);
      outjectDataModels(bean);
   }

   private void injectDataModelSelection(Object bean)
   {
      for ( Method dataModelGetter: dataModelGetters )
      {
         Annotation dataModelAnn = dataModelGetterAnnotations.get(dataModelGetter);
         DataBinder wrapper = createWrapper(dataModelAnn);
         final String name = toName( wrapper.getVariableName(dataModelAnn), dataModelGetter );
         injectDataModelSelection( bean, name, null, wrapper, dataModelAnn );
      }
      for ( Field dataModelField: dataModelFields )
      {
         Annotation dataModelAnn = dataModelFieldAnnotations.get(dataModelField);
         DataBinder wrapper = createWrapper(dataModelAnn);
         final String name = toName( wrapper.getVariableName(dataModelAnn), dataModelField );
         injectDataModelSelection( bean, name, dataModelField, wrapper, dataModelAnn );
      }
   }

   private void injectDataModelSelection(Object bean, String name, Field dataModelField, DataBinder wrapper, Annotation dataModelAnn)
   {
      ScopeType scope = wrapper.getVariableScope(dataModelAnn);

      Object dataModel = getOutContext(scope).get(name);
      if ( dataModel != null )
      {

         if (dataModelField!=null)
         {
            setFieldValue( bean, dataModelField, name, wrapper.getWrappedData(dataModelAnn, dataModel) ); //for PAGE scope datamodels (does not work for properties!)
         }

         Object selectedIndex = wrapper.getSelection(dataModelAnn, dataModel);

         if ( log.isDebugEnabled() ) log.debug( "selected row: " + selectedIndex );

         if ( selectedIndex!=null )
         {
            Method setter = dataModelSelectionSetters.get(name);
            if (setter != null)
            {
               Annotation dataModelSelectionAnn = dataModelSelectionSetterAnnotations.get(setter);
               Object selection = createUnwrapper(dataModelSelectionAnn).getSelection(dataModelSelectionAnn, dataModel);
               setPropertyValue(bean, setter, name, selection);
            }
            Field field = dataModelSelectionFields.get(name);
            if (field != null)
            {
               Annotation dataModelSelectionAnn = dataModelSelectionFieldAnnotations.get(field);
               Object selection = createUnwrapper(dataModelSelectionAnn).getSelection(dataModelSelectionAnn, dataModel);
               setFieldValue(bean, field, name, selection);
            }
         }

      }
   }

   private void outjectDataModels(Object bean)
   {
      for ( Method dataModelGetter: dataModelGetters )
      {
         final Object list;
         final String name;
         Annotation dataModelAnn = dataModelGetterAnnotations.get(dataModelGetter);
         DataBinder wrapper = createWrapper(dataModelAnn);
         name = toName( wrapper.getVariableName(dataModelAnn), dataModelGetter );
         list = getPropertyValue( bean, dataModelGetter, name );
         outjectDataModelList( name, list, wrapper, dataModelAnn );
      }

      for ( Field dataModelField: dataModelFields )
      {
         final Object list;
         final String name;
         Annotation dataModelAnn = dataModelFieldAnnotations.get(dataModelField);
         DataBinder wrapper = createWrapper(dataModelAnn);
         name = toName( wrapper.getVariableName(dataModelAnn), dataModelField );
         list = getFieldValue( bean, dataModelField, name );
         outjectDataModelList( name, list, wrapper, dataModelAnn );
      }

   }

   private static DataBinder createWrapper(Annotation dataModelAnn)
   {
      try
      {
         return dataModelAnn.annotationType().getAnnotation(DataBinderClass.class).value().newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static DataSelector createUnwrapper(Annotation dataModelAnn)
   {
      try
      {
         return dataModelAnn.annotationType().getAnnotation(DataSelectorClass.class).value().newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void outjectDataModelList(String name, Object list, DataBinder wrapper, Annotation dataModelAnn)
   {

      ScopeType scope = wrapper.getVariableScope(dataModelAnn);

      Context context = getOutContext(scope);
      Object existingDataModel = context.get(name);
      boolean dirty = existingDataModel == null || scope==PAGE ||
            wrapper.isDirty(dataModelAnn, existingDataModel, list);

      if ( dirty )
      {
         if ( list != null )
         {
            context.set( name, wrapper.wrap(dataModelAnn, list) );
         }
         else
         {
            context.remove( name );
         }
      }

   }

   private Context getOutContext(ScopeType specifiedScope) {
      ScopeType scope = this.scope;
      if (scope==STATELESS)
      {
         scope = EVENT;
      }
      if (specifiedScope!=UNSPECIFIED)
      {
         scope = specifiedScope;
      }
      return scope.getContext();
   }

   private void injectMethods(Object bean, boolean enforceRequired)
   {
      for (Method method : getInMethods())
      {
         In in = method.getAnnotation(In.class);
         String name = toName(in.value(), method);
         setPropertyValue( bean, method, name, getInstanceToInject(in, name, bean, enforceRequired) );
      }
   }

   private void disinjectMethods(Object bean)
   {
      for (Method method : getInMethods())
      {
         if ( !method.getParameterTypes()[0].isPrimitive() )
         {
            String name = toName( method.getAnnotation(In.class).value(), method );
            setPropertyValue(bean, method, name, null);
         }
      }
   }

   private void injectFields(Object bean, boolean enforceRequired)
   {
      for (Field field : getInFields())
      {
         In in = field.getAnnotation(In.class);
         String name = toName(in.value(), field);
         setFieldValue( bean, field, name, getInstanceToInject(in, name, bean, enforceRequired) );
      }
   }

   private void disinjectFields(Object bean)
   {
      for (Field field : getInFields())
      {
         if ( !field.getType().isPrimitive() )
         {
            String name = toName( field.getAnnotation(In.class).value(), field );
            setFieldValue(bean, field, name, null);
         }
      }
   }

   private void outjectFields(Object bean, boolean enforceRequired)
   {
      for (Field field : getOutFields())
      {
         Out out = field.getAnnotation(Out.class);
         if (out != null)
         {
            String name = toName(out.value(), field);
            setOutjectedValue( out, name, getFieldValue(bean, field, name), enforceRequired );
         }
      }
   }

   private void outjectMethods(Object bean, boolean enforceRequired)
   {
      for (Method method : getOutMethods())
      {
         Out out = method.getAnnotation(Out.class);
         if (out != null)
         {
            String name = toName(out.value(), method);
            setOutjectedValue( out, name, getPropertyValue(bean, method, name), enforceRequired );
         }
      }
   }

   private void setOutjectedValue(Out out, String name, Object value, boolean enforceRequired)
   {
      if (value==null && enforceRequired && out.required())
      {
         throw new RequiredException(
               "Out attribute requires value for component: " +
               getAttributeMessage(name)
            );
      }
      else
      {
         Component component = null;
         if ( out.scope()==UNSPECIFIED )
         {
            component = Component.forName(name);
            if (value!=null && component!=null)
            {
               if ( !component.isInstance(value) )
               {
                  throw new IllegalArgumentException(
                        "attempted to bind an Out attribute of the wrong type to: " +
                        getAttributeMessage(name)
                     );
               }
            }
         }

         Context context = component==null ?
               getOutContext( out.scope() ) :
               component.getScope().getContext();

         if (value==null)
         {
            context.remove(name);
         }
         else
         {
            context.set(name, value);
         }
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
            for (Class intfc: businessInterfaces)
            {
               if (intfc.isAssignableFrom(clazz))
               {
                  return true;
               }
            }
            return false;
      }
   }

   private static Set<Class> getBusinessInterfaces(Class clazz)
   {
      Set<Class> result = new HashSet<Class>();

      if ( clazz.isAnnotationPresent(Local.class) )
      {
         Local local = (Local) clazz.getAnnotation(Local.class);
         for ( Class iface: local.value() ) {
            result.add(iface);
         }
      }

      if ( clazz.isAnnotationPresent(Remote.class) )
      {
         Remote remote = (Remote) clazz.getAnnotation(Remote.class);
         for ( Class iface: remote.value() )
         {
            result.add(iface);
         }
      }

      for ( Class iface: clazz.getInterfaces() )
      {
         if ( iface.isAnnotationPresent(Local.class) || iface.isAnnotationPresent(Remote.class) )
         {
            result.add(iface);
         }
      }

      if ( result.size() == 0 ) {
         for ( Class iface: clazz.getInterfaces() )
         {
            if ( !isExcludedLocalInterfaceName( iface.getName() ) )
            {
               result.add(iface);
            }
         }
      }

       return result;
   }

   public Set<Class> getBusinessInterfaces()
   {
      return businessInterfaces;
   }

    private static boolean isExcludedLocalInterfaceName(String name) {
        return name.equals("java.io.Serializable") ||
               name.equals("java.io.Externalizable") ||
               name.startsWith("javax.ejb.");
    }



   private Object getFieldValue(Object bean, Field field, String name)
   {
      try {
         return Reflections.get(field, bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not get field value: " + getAttributeMessage(name), e);
      }
   }

   private Object getPropertyValue(Object bean, Method method, String name)
   {
      try {
         return Reflections.invoke(method, bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not get property value: " + getAttributeMessage(name), e);
      }
   }

   private void setPropertyValue(Object bean, Method method, String name, Object value)
   {
      try
      {
         Reflections.invoke(method, bean, value );
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not set property value: " + getAttributeMessage(name), e);
      }
   }

   private void setFieldValue(Object bean, Field field, String name, Object value)
   {
      try
      {
         Reflections.set(field, bean, value);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not set field value: " + getAttributeMessage(name), e);
      }
   }

   public static Component forName(String name)
   {
      return (Component) Contexts.getApplicationContext().get( name + ".component" );
   }

   public static Object getInstance(Class<?> clazz)
   {
      return getInstance(clazz, true);
   }

   public static Object getInstance(Class<?> clazz, boolean create)
   {
      return getInstance( Seam.getComponentName(clazz), create );
   }

   public static Object getInstance(Class<?> clazz, ScopeType scope, boolean create)
   {
      return getInstance( Seam.getComponentName(clazz), scope, create );
   }

   public static Object getInstance(String name)
   {
      return getInstance(name, true);
   }

   public static Object getInstance(String name, boolean create)
   {
      Object result = Contexts.lookupInStatefulContexts(name);
      result = getInstance(name, create, result);
      return result;
   }

   public static Object getInstance(String name, ScopeType scope, boolean create)
   {
      Object result = scope.getContext().get(name);
      result = getInstance(name, create, result);
      return result;
   }

   private static Object getInstance(String name, boolean create, Object result) {
      Component component = Component.forName(name);

      if (result==null && create)
      {
        result = getInstanceFromFactory(name);
        if (result==null)
        {
           if (component==null)
           {
              //needed when this method is called by JSF
              if ( log.isDebugEnabled() ) log.debug("seam component not found: " + name);
           }
           else
           {
              result = component.newInstance();
           }
        }
      }

      if (result!=null)
      {
         if (component!=null)
         {
            if ( !component.isInstance(result) )
            {
               throw new IllegalArgumentException( "value of context variable is not an instance of the component bound to the context variable: " + name );
            }
            result = component.unwrap(result);
         }
      }

      return result;

   }

   public static Object getInstanceFromFactory(String name)
   {
      Init init = Init.instance();
      if (init==null) //for unit tests, yew!
      {
         return null;
      }
      else
      {
         Init.FactoryMethod factoryMethod = init.getFactory(name);
         Init.FactoryMethodBinding methodBinding = init.getFactoryMethodBinding(name);
         if (methodBinding!=null) //let the XML take precedence
         {
            Object result = methodBinding.methodBinding.invoke( FacesContext.getCurrentInstance(), null );
            return handleFactoryMethodResult(name, null, result, factoryMethod.scope);
         }
         else if (factoryMethod!=null)
         {
            Object factory = Component.getInstance( factoryMethod.component.getName(), true );
            Object result = factoryMethod.component.callComponentMethod(factory, factoryMethod.method);
            return handleFactoryMethodResult(name, factoryMethod.component, result, factoryMethod.scope);
         }
         else
         {
            return null;
         }
      }
   }

   private static Object handleFactoryMethodResult(String name, Component component, Object result, ScopeType scope)
   {
      Object value = Contexts.lookupInStatefulContexts(name); //see if a value was outjected by the factory method
      if (value==null) //usually a factory method returning a value
      {
         if (scope==UNSPECIFIED)
         {
            if (component==null)
            {
               throw new IllegalArgumentException("no scope specified for factory method defined in components.xml: " + name);
            }
            else //an @Factory method defaults to the same scope as the component
            {
               scope = component.getScope();
               if (scope==STATELESS) scope=EVENT;
            }
         }
         scope.getContext().set(name, result);
         return result;
      }
      else //usually a factory method with a void return type
      {
         if (scope!=UNSPECIFIED)
         {
            throw new IllegalArgumentException("factory method with defined scope outjected a value: " + name);
         }
         return value;
      }
   }

   public Object newInstance()
   {
      if ( log.isDebugEnabled() ) log.debug("instantiating Seam component: " + name);

      Object instance;
      try
      {
         instance = instantiate();
      }
      catch (Exception e)
      {
         throw new InstantiationException("Could not instantiate Seam component: " + name, e);
      }

      if ( getScope()!=STATELESS )
      {
         getScope().getContext().set(name, instance); //put it in the context _before_ calling the create method
         callCreateMethod(instance);
         if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postCreate." + name);
      }

      return instance;
   }

   public void callCreateMethod(Object instance)
   {
      if ( hasCreateMethod() )
      {
         callComponentMethod( instance, getCreateMethod() );
      }
   }

   public void callDestroyMethod(Object instance)
   {
      if ( hasDestroyMethod() )
      {
         callComponentMethod( instance, getDestroyMethod() );
      }
   }

   public void callPreDestroyMethod(Object instance)
   {
      if ( hasPreDestroyMethod() )
      {
         callComponentMethod( instance, getPreDestroyMethod() );
      }
   }

   public void callPostConstructMethod(Object instance)
   {
      if ( hasPostConstructMethod() )
      {
         callComponentMethod( instance, getPostConstructMethod() );
      }
   }

   public void callPrePassivateMethod(Object instance)
   {
      if ( hasPrePassivateMethod() )
      {
         callComponentMethod( instance, getPrePassivateMethod() );
      }
   }

   public void callPostActivateMethod(Object instance)
   {
      if ( hasPostActivateMethod() )
      {
         callComponentMethod( instance, getPostActivateMethod() );
      }
   }

   public Object callComponentMethod(Object instance, Method method) {
      Class[] paramTypes = method.getParameterTypes();
      String createMethodName = method.getName();
      try
      {
         Method interfaceMethod = instance.getClass().getMethod(createMethodName, paramTypes);
         if ( paramTypes.length==0 )
         {
            return Reflections.invokeAndWrap( interfaceMethod, instance );
         }
         else {
            return Reflections.invokeAndWrap( interfaceMethod, instance, this );
         }
      }
      catch (NoSuchMethodException e)
      {
         String message = "method not found: " + method.getName() + " for component: " + name;
         if ( getType().isSessionBean() )
         {
             message += " (check that it is declared on the session bean business interface)";
         }
         throw new IllegalArgumentException(message, e);
      }
   }

   private Object unwrap(Object instance)
   {
      if ( hasUnwrapMethod() )
      {
         return callComponentMethod( instance, getUnwrapMethod() );
      }
      else
      {
         return instance;
      }
   }

   private Object getInstanceToInject(In in, String name, Object bean, boolean enforceRequired)
   {
      Object result;
      if ( name.startsWith("#") )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject with EL expression: " + name);
         }
         FacesContext facesCtx = FacesContext.getCurrentInstance();
         Application application = facesCtx.getApplication();
         result = application.createValueBinding(name).getValue(facesCtx);
      }
      else if ( in.scope()==UNSPECIFIED )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject with hierarchical context search: " + name);
         }
         result = getInstance( name, in.create() );
      }
      else
      {
         if ( in.create() )
         {
            throw new IllegalArgumentException(
                  "cannot combine create=true with explicit scope on @In: " +
                  getAttributeMessage(name)
               );
         }
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject from specified context: " + name + ", scope: " + scope);
         }
         result = in.scope().getContext().get(name);
      }

      if ( result==null && enforceRequired && in.required() )
      {
         throw new RequiredException(
               "In attribute requires value for component: " +
               getAttributeMessage(name)
            );
      }
      else
      {
         return result;
      }
   }

   private String getAttributeMessage(String attributeName)
   {
      return getName() + '.' + attributeName;
   }

   private static String toName(String name, Method method)
   {
      //TODO: does not handle "isFoo"
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
      en.setSuperclass( type==JAVA_BEAN ? beanClass : Object.class );
      Set<Class> interfaces = new HashSet<Class>();
      if ( type.isSessionBean() )
      {
         interfaces.addAll( getBusinessInterfaces() );
      }
      else
      {
         interfaces.add(HttpSessionActivationListener.class);
      }
      en.setInterfaces( interfaces.toArray( new Class[0] ) );
      return (Class<Factory>) en.createClass();
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

   public boolean isMutable()
   {
      return mutable;
   }

   public static interface InitialValue
   {
      Object getValue(Class type);
   }

   public static class ConstantInitialValue implements InitialValue
   {
      private Object value;

      public ConstantInitialValue(PropertyValue propertyValue, Class parameterClass, Type parameterType)
      {
         this.value = Conversions.getConverter(parameterClass).toObject(propertyValue, parameterType);
      }

      public Object getValue(Class type)
      {
         return value;
      }

      public String toString()
      {
         return "ConstantInitialValue(" + value + ")";
      }

   }

   public static class ELInitialValue implements InitialValue
   {
      private String expression;
      //private ValueBinding vb;
      private Conversions.Converter converter;
      private Type parameterType;

      public ELInitialValue(PropertyValue propertyValue, Class parameterClass, Type parameterType)
      {
         this.expression = propertyValue.getSingleValue();
         this.parameterType = parameterType;
         try
         {
            this.converter = Conversions.getConverter(parameterClass);
         }
         catch (IllegalArgumentException iae) {
            //no converter for the type
         }
         //vb = FacesContext.getCurrentInstance().getApplication().createValueBinding(expression);
      }

      public Object getValue(Class type)
      {
         Object value;
         if ( type.equals(ValueBinding.class) )
         {
            value = createValueBinding();
         }
         else if ( type.equals(MethodBinding.class) )
         {
            value = createMethodBinding();
         }
         else
         {
            value = createValueBinding().getValue( FacesContext.getCurrentInstance() );
         }

         if (converter!=null && value instanceof String)
         {
            return converter.toObject( new Conversions.FlatPropertyValue( (String) value ), parameterType );
         }
         else if (converter!=null && value instanceof String[])
         {
            return converter.toObject( new Conversions.MultiPropertyValue( (String[]) value ), parameterType );
         }
         else
         {
            return value;
         }
      }

      private ValueBinding createValueBinding()
      {
         return FacesContext.getCurrentInstance().getApplication()
               .createValueBinding( expression );
      }

      private MethodBinding createMethodBinding()
      {
         return FacesContext.getCurrentInstance().getApplication()
               .createMethodBinding( expression, null );
      }

      public String toString()
      {
         return "ELInitialValue(" + expression + ")";
      }

   }

   public Method getPostActivateMethod()
   {
      return postActivateMethod;
   }

   public Method getPrePassivateMethod()
   {
      return prePassivateMethod;
   }

   public Method getPostConstructMethod()
   {
      return postConstructMethod;
   }

   public Method getPreDestroyMethod()
   {
      return preDestroyMethod;
   }

}
