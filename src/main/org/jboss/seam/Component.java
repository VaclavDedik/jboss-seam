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
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.ScopeType.UNSPECIFIED;
import static org.jboss.seam.util.EJB.INTERCEPTORS;
import static org.jboss.seam.util.EJB.LOCAL;
import static org.jboss.seam.util.EJB.PERSISTENCE_CONTEXT;
import static org.jboss.seam.util.EJB.POST_ACTIVATE;
import static org.jboss.seam.util.EJB.POST_CONSTRUCT;
import static org.jboss.seam.util.EJB.PRE_DESTROY;
import static org.jboss.seam.util.EJB.PRE_PASSIVATE;
import static org.jboss.seam.util.EJB.REMOTE;
import static org.jboss.seam.util.EJB.REMOVE;
import static org.jboss.seam.util.EJB.value;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.servlet.http.HttpSessionActivationListener;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.hibernate.validator.ClassValidator;
import org.jboss.seam.annotations.Asynchronous;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.DataBinderClass;
import org.jboss.seam.annotations.DataSelectorClass;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Interceptors;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Rollback;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Mutable;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.databinding.DataBinder;
import org.jboss.seam.databinding.DataSelector;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.intercept.ClientSideInterceptor;
import org.jboss.seam.intercept.Interceptor;
import org.jboss.seam.intercept.JavaBeanInterceptor;
import org.jboss.seam.intercept.Proxy;
import org.jboss.seam.interceptors.AsynchronousInterceptor;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.ConversationalInterceptor;
import org.jboss.seam.interceptors.EventInterceptor;
import org.jboss.seam.interceptors.ManagedEntityIdentityInterceptor;
import org.jboss.seam.interceptors.MethodContextInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.RollbackInterceptor;
import org.jboss.seam.interceptors.SecurityInterceptor;
import org.jboss.seam.interceptors.SynchronizationInterceptor;
import org.jboss.seam.interceptors.TransactionInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
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
@SuppressWarnings("deprecation")
public class Component
{
   public static final String PROPERTIES = "org.jboss.seam.properties";

   private static final LogProvider log = Logging.getLogProvider(Component.class);

   private ComponentType type;
   private String name;
   private ScopeType scope;
   private Class<?> beanClass;
   private String jndiName;
   private InterceptionType interceptionType;
   private boolean startup;
   private String[] dependencies;
   private boolean synchronize;
   private long timeout;

   private Set<Class> businessInterfaces;

   private Method destroyMethod;
   private Method createMethod;
   private Method unwrapMethod;
   private Method preDestroyMethod;
   private Method postConstructMethod;
   private Method prePassivateMethod;
   private Method postActivateMethod;
   private Map<String, Method> removeMethods = new HashMap<String, Method>();
   private Set<Method> validateMethods = new HashSet<Method>();
   private Set<Method> lifecycleMethods = new HashSet<Method>();
   private Set<Method> conversationManagementMethods = new HashSet<Method>();
   
   private List<BijectedAttribute<In>> inAttributes = new ArrayList<BijectedAttribute<In>>();
   private List<BijectedAttribute<Out>> outAttributes = new ArrayList<BijectedAttribute<Out>>();
   private List<BijectedAttribute> parameterSetters = new ArrayList<BijectedAttribute>();
   private List<BijectedAttribute> dataModelGetters = new ArrayList<BijectedAttribute>();
   private Map<String, BijectedAttribute> dataModelSelectionSetters = new HashMap<String, BijectedAttribute>();
   
   private List<Interceptor> interceptors = new ArrayList<Interceptor>();
   private List<Interceptor> clientSideInterceptors = new ArrayList<Interceptor>();

   private Map<Method, InitialValue> initializerSetters = new HashMap<Method, InitialValue>();
   private Map<Field, InitialValue> initializerFields = new HashMap<Field, InitialValue>();

   private List<Field> logFields = new ArrayList<Field>();
   private List<org.jboss.seam.log.Log> logInstances = new ArrayList<org.jboss.seam.log.Log>();

   private Hashtable<Locale, ClassValidator> validators = new Hashtable<Locale, ClassValidator>();

   private Class<Factory> factory;

   //only used for tests
   public Component(Class<?> clazz)
   {
      this( clazz, Seam.getComponentName(clazz) );
   }

   // only used for tests
   public Component(Class<?> clazz, String componentName)
   {
      this(clazz, componentName, Seam.getComponentScope(clazz), null);
   }

   // only used for tests
   public Component(Class<?> clazz, Context applicationContext)
   {
      this( clazz, Seam.getComponentName(clazz), Seam.getComponentScope(clazz), null, applicationContext );
   }

   public Component(Class<?> clazz, String componentName, ScopeType componentScope, String jndiName)
   {
      this(clazz, componentName, componentScope, jndiName, Contexts.getApplicationContext());
   }

   private Component(Class<?> clazz, String componentName, ScopeType componentScope, String componentJndiName, Context applicationContext)
   {
      beanClass = clazz;
      name = componentName;
      scope = componentScope;
      type = Seam.getComponentType(beanClass);
      interceptionType = Seam.getInterceptionType(beanClass);

      initNamespaces(componentName, applicationContext);

      checkScopeForComponentType();

      jndiName = componentJndiName == null ?
            getJndiName(applicationContext) : componentJndiName;

      startup = beanClass.isAnnotationPresent(Startup.class);
      if (startup)
      {
         if (scope!=SESSION && scope!=APPLICATION)
         {
            throw new IllegalArgumentException("@Startup only supported for SESSION or APPLICATION scoped components: " + name);
         }
         dependencies = getBeanClass().getAnnotation(Startup.class).depends();
      }

      synchronize = ( scope==SESSION /*&& ! beanClass.isAnnotationPresent(ReadOnly.class)*/ ) ||
            beanClass.isAnnotationPresent(Synchronized.class);
      if (synchronize)
      {
         if (scope==STATELESS)
         {
            throw new IllegalArgumentException("@Synchronized not meaningful for stateless components: " + name);
         }
         timeout = beanClass.isAnnotationPresent(Synchronized.class) ?
               beanClass.getAnnotation(Synchronized.class).timeout() :
               Synchronized.DEFAULT_TIMEOUT;
      }

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

   }

   private void initNamespaces(String componentName, Context applicationContext)
   {
      if (applicationContext!=null) //for unit tests!
      {
         Init init = (Init) applicationContext.get( Seam.getComponentName(Init.class) );
         if (init!=null)
         {
            Namespace namespace = init.getRootNamespace();
            StringTokenizer tokens = new StringTokenizer(componentName, ".");
            StringBuffer path = new StringBuffer();
            while ( tokens.hasMoreTokens() )
            {
               String token = tokens.nextToken();
               path.append(token).append('.');
               if ( tokens.hasMoreTokens() && !namespace.hasChild(token) )
               {
                  namespace.addChild( token, new Namespace(path.toString()) );
               }
               namespace = namespace.getChild(token);
            }
         }
      }
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
      if ( type==STATEFUL_SESSION_BEAN && ( destroyMethod==null || !removeMethods.values().contains(destroyMethod) ) )
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

            /*if ( type==ENTITY_BEAN )
            {
               throw new IllegalArgumentException("can not configure entity beans: " + name);
            }*/

            String propertyName = key.substring( name.length()+1, key.length() );
            Method setterMethod = Reflections.getSetterMethod(beanClass, propertyName);
            if (setterMethod!=null)
            {
               if ( !setterMethod.isAccessible() ) setterMethod.setAccessible(true);
               Class parameterClass = setterMethod.getParameterTypes()[0];
               Type parameterType = setterMethod.getGenericParameterTypes()[0];
               initializerSetters.put( setterMethod, getTopInitialValue(propertyValue, parameterClass, parameterType) );
            }
            else
            {
               Field field = Reflections.getField(beanClass, propertyName);
               if ( !field.isAccessible() ) field.setAccessible(true);
               initializerFields.put( field, getInitialValue(propertyValue, field.getType(), field.getGenericType()) );
            }
        }

      }
   }

   private InitialValue getTopInitialValue(Conversions.PropertyValue propertyValue, Class parameterClass, Type parameterType)
   {
      //note that org.jboss.seam.core.init.jndiPattern looks like an EL expression but is not one!
      if ( propertyValue.isExpression() && getBeanClass().equals(Init.class) )
      {
         return new ConstantInitialValue(propertyValue, parameterClass, parameterType);
      }
      else
      {
         return getInitialValue(propertyValue, parameterClass, parameterType);
      }
   }

   private static InitialValue getInitialValue(Conversions.PropertyValue propertyValue, Class parameterClass, Type parameterType)
   {
      if ( propertyValue.isExpression() )
      {
         return new ELInitialValue(propertyValue, parameterClass, parameterType);
      }
      else if ( propertyValue.isMultiValued() )
      {
         return new ListInitialValue(propertyValue, parameterClass, parameterType);
      }
      else if ( propertyValue.isAssociativeValued() )
      {
         return new MapInitialValue(propertyValue, parameterClass, parameterType);
      }
      else
      {
         return new ConstantInitialValue(propertyValue, parameterClass, parameterType);
      }
   }


   private void initMembers(Class<?> clazz, Context applicationContext)
   {
      Map<Method, Annotation> selectionSetters = new HashMap<Method, Annotation>();
      Map<Field, Annotation> selectionFields = new HashMap<Field, Annotation>();
      Set<String> dataModelNames = new HashSet<String>();

      for (;clazz!=Object.class; clazz = clazz.getSuperclass())
      {

         for (Method method: clazz.getDeclaredMethods())
         {
            if ( method.isAnnotationPresent(IfInvalid.class) )
            {
               validateMethods.add(method);
            }
            if ( method.isAnnotationPresent(REMOVE) )
            {
               removeMethods.put( method.getName(), method );
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
               lifecycleMethods.add(method);
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
               lifecycleMethods.add(method);
            }
            if ( method.isAnnotationPresent(In.class) )
            {
               In in = method.getAnnotation(In.class);
               String name = toName( in.value(), method );
               inAttributes.add( new BijectedMethod(name, method, in) );
            }
            if ( method.isAnnotationPresent(Out.class) )
            {
               Out out = method.getAnnotation(Out.class);
               String name = toName( out.value(), method );
               outAttributes.add( new BijectedMethod(name, method, out) );
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
               Observer observer = method.getAnnotation(Observer.class);
               for ( String eventType : observer.value() )
               {
                  if ( eventType.length()==0 ) eventType = method.getName(); //TODO: new defaulting rule to map @Observer onFooEvent() -> event type "fooEvent"
                  init.addObserverMethod( eventType, method, this, observer.create() );
               }
            }
            if ( method.isAnnotationPresent(RequestParameter.class) )
            {
               RequestParameter rp = method.getAnnotation(RequestParameter.class);
               String name = toName( rp.value(), method );
               parameterSetters.add( new BijectedMethod(name, method, rp) );
            }
            if ( method.isAnnotationPresent(PRE_PASSIVATE) )
            {
               prePassivateMethod = method;
               lifecycleMethods.add(method);
            }
            if ( method.isAnnotationPresent(POST_ACTIVATE) )
            {
               postActivateMethod = method;
               lifecycleMethods.add(method);
            }
            if ( method.isAnnotationPresent(POST_CONSTRUCT) )
            {
               postConstructMethod = method;
               lifecycleMethods.add(method);
            }
            if ( method.isAnnotationPresent(PRE_DESTROY) )
            {
               preDestroyMethod = method;
               lifecycleMethods.add(method);
            }
            if ( method.isAnnotationPresent(PERSISTENCE_CONTEXT) )
            {
               if ( !type.isSessionBean() && type!=MESSAGE_DRIVEN_BEAN )
               {
                  throw new IllegalArgumentException("@PersistenceContext may only be used on session bean or message driven bean components: " + name);
               }
            }
            if ( method.isAnnotationPresent(Begin.class) || 
                 method.isAnnotationPresent(End.class) || 
                 method.isAnnotationPresent(StartTask.class) ||
                 method.isAnnotationPresent(BeginTask.class) ||
                 method.isAnnotationPresent(EndTask.class) ) 
            {
               conversationManagementMethods.add(method);
            }
            
            for ( Annotation ann: method.getAnnotations() )
            {
               if ( ann.annotationType().isAnnotationPresent(DataBinderClass.class) )
               {
                  String name = toName( createWrapper(ann).getVariableName(ann), method );
                  dataModelGetters.add( new BijectedMethod(name, method, ann) );
                  dataModelNames.add(name);
               }
               if ( ann.annotationType().isAnnotationPresent(DataSelectorClass.class) )
               {
                  selectionSetters.put(method, ann);
               }
            }

            if ( !method.isAccessible() )
            {
               method.setAccessible(true);
            }

         }

         for (Field field: clazz.getDeclaredFields())
         {

            if ( !field.isAccessible() )
            {
               field.setAccessible(true);
            }

            if ( field.isAnnotationPresent(In.class) )
            {
               In in = field.getAnnotation(In.class);
               String name = toName( in.value(), field );
               inAttributes.add( new BijectedField(name, field, in) );
            }
            if ( field.isAnnotationPresent(Out.class) )
            {
               Out out = field.getAnnotation(Out.class);
               String name = toName( out.value(), field );
               outAttributes.add(new BijectedField(name, field, out) );
            }
            if ( field.isAnnotationPresent(DataModel.class) ) //TODO: generalize
            {
               checkDataModelScope( field.getAnnotation(DataModel.class) );
            }
            if ( field.isAnnotationPresent(RequestParameter.class) )
            {
               RequestParameter rp = field.getAnnotation(RequestParameter.class);
               String name = toName( rp.value(), field );
               parameterSetters.add( new BijectedField(name, field, rp) );
            }
            if ( field.isAnnotationPresent(org.jboss.seam.annotations.Logger.class) )
            {
               String category = field.getAnnotation(org.jboss.seam.annotations.Logger.class).value();
               org.jboss.seam.log.Log logInstance;
               if ( "".equals( category ) )
               {
                  logInstance = org.jboss.seam.log.Logging.getLog(beanClass);
               }
               else
               {
                  logInstance = org.jboss.seam.log.Logging.getLog(category);
               }
               if ( Modifier.isStatic( field.getModifiers() ) )
               {
                  Reflections.setAndWrap(field, null, logInstance);
               }
               else
               {
                  logFields.add(field);
                  logInstances.add(logInstance);
               }
            }
            for ( Annotation ann: field.getAnnotations() )
            {
               if ( ann.annotationType().isAnnotationPresent(DataBinderClass.class) )
               {
                  String name = toName( createWrapper(ann).getVariableName(ann), field );
                  dataModelGetters.add( new BijectedField(name, field, ann) );
                  dataModelNames.add(name);
               }
               if ( ann.annotationType().isAnnotationPresent(DataSelectorClass.class) )
               {
                  selectionFields.put(field, ann);
               }
            }

         }

      }

      final boolean hasMultipleDataModels = dataModelGetters.size() > 1;
      String defaultDataModelName = null;
      if ( !hasMultipleDataModels )
      {
         if ( !dataModelGetters.isEmpty() )
         {
            defaultDataModelName = dataModelGetters.get(0).getName();
         }
      }

      for (Map.Entry<Method, Annotation> annotatedMethod: selectionSetters.entrySet())
      {
         Method method = annotatedMethod.getKey();
         Annotation ann = annotatedMethod.getValue();
         String name = getDataModelSelectionName(dataModelNames, hasMultipleDataModels, defaultDataModelName, ann);
         Object existing = dataModelSelectionSetters.put( name, new BijectedMethod(name, method, ann) );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelection setters for: " + name);
         }
      }

      for (Map.Entry<Field, Annotation> annotatedField: selectionFields.entrySet())
      {
         Field field = annotatedField.getKey();
         Annotation ann = annotatedField.getValue();
         String name = getDataModelSelectionName(dataModelNames, hasMultipleDataModels, defaultDataModelName, ann);
         Object existing = dataModelSelectionSetters.put( name, new BijectedField(name, field, ann) );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelection fields for: " + name);
         }
      }

   }

   private String getDataModelSelectionName(Set<String> dataModelNames, boolean hasMultipleDataModels, String defaultDataModelName, Annotation ann)
   {
      String name = createUnwrapper(ann).getVariableName(ann);
      if ( name.length() == 0 )
      {
         if ( hasMultipleDataModels )
         {
            throw new IllegalStateException( "Missing value() for @DataModelSelection with multiple @DataModels" );
         }
         if ( defaultDataModelName==null )
         {
            throw new IllegalStateException("No @DataModel for @DataModelSelection: " + name);
         }
         return defaultDataModelName;
      }
      else
      {
         if ( !dataModelNames.contains(name) )
         {
            throw new IllegalStateException("No @DataModel for @DataModelSelection: " + name);
         }
         return name;
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

      for ( Annotation annotation: beanClass.getAnnotations() )
      {
         if ( annotation.annotationType().isAnnotationPresent(INTERCEPTORS) )
         {
            Class[] classes = value( annotation.annotationType().getAnnotation(INTERCEPTORS) );
            addInterceptor( new Interceptor(classes, annotation, this) );
         }
         if ( annotation.annotationType().isAnnotationPresent(Interceptors.class) )
         {
            Class[] classes = annotation.annotationType().getAnnotation(Interceptors.class).value();
            addInterceptor( new Interceptor(classes, annotation, this) );
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
               SortItem<Interceptor> sortItem = ht.get(cl);
               if (sortItem!=null) si.getAround().add( sortItem );
            }
            for (Class<?> cl : Arrays.asList( interceptorAnn.within() ) )
            {
               SortItem<Interceptor> sortItem = ht.get(cl);
               if (sortItem!=null) si.getWithin().add( sortItem );
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
      if (synchronize)
      {
         addInterceptor( new Interceptor( new SynchronizationInterceptor(), this ) );
      }
      if (
            ( getType().isEjb() && businessInterfaceHasAnnotation(Asynchronous.class) ) ||
            ( getType()==JAVA_BEAN && beanClassHasAnnotation(Asynchronous.class) )
         )
      {
         addInterceptor( new Interceptor( new AsynchronousInterceptor(), this ) );
      }
      if ( getType()==STATEFUL_SESSION_BEAN )
      {
         addInterceptor( new Interceptor( new RemoveInterceptor(), this ) );
      }
      if ( getType()!=ENTITY_BEAN )
      {
         addInterceptor( new Interceptor( new MethodContextInterceptor(), this ) );
      }
      if ( beanClassHasAnnotation(RaiseEvent.class) )
      {
         addInterceptor( new Interceptor( new EventInterceptor(), this ) );
      }
      if ( beanClassHasAnnotation(Conversational.class) )
      {
         addInterceptor( new Interceptor( new ConversationalInterceptor(), this ) );
      }
      if ( Contexts.isApplicationContextActive() ) //ugh, for unit tests
      {
         if ( Init.instance().isJbpmInstalled() )
         {
            addInterceptor( new Interceptor( new BusinessProcessInterceptor(), this ) );
         }
      }
      if ( !conversationManagementMethods.isEmpty() )
      {
         addInterceptor( new Interceptor( new ConversationInterceptor(), this ) );
      }
      if ( needsInjection() || needsOutjection() )
      {
         addInterceptor( new Interceptor( new BijectionInterceptor(), this ) );
      }
      if ( beanClassHasAnnotation(IfInvalid.class) )
      {
         addInterceptor( new Interceptor( new ValidationInterceptor(), this ) );
      }
      if ( getType()==JAVA_BEAN || beanClassHasAnnotation(Rollback.class) )
      {
         addInterceptor( new Interceptor( new RollbackInterceptor(), this ) );
      }
      if ( getType()==JAVA_BEAN && beanClassHasAnnotation(Transactional.class))
      {
         addInterceptor( new Interceptor( new TransactionInterceptor(), this ) );
      }
      if ( getScope()==CONVERSATION )
      {
         addInterceptor( new Interceptor( new ManagedEntityIdentityInterceptor(), this ) );
      }
      if ( beanClassHasAnnotation(Restrict.class) )
      {
        addInterceptor( new Interceptor( new SecurityInterceptor(), this ) );
      }
   }

   private static boolean hasAnnotation(Class clazz, Class annotationType)
   {
      if ( clazz.isAnnotationPresent(annotationType) )
      {
         return true;
      }
      else
      {
         for ( Method method: clazz.getMethods() )
         {
            if ( method.isAnnotationPresent(annotationType) )
            {
               return true;
            }
         }
         return false;
      }
   }

   public boolean beanClassHasAnnotation(Class annotationType)
   {
      return hasAnnotation( getBeanClass(), annotationType );
   }

   public boolean businessInterfaceHasAnnotation(Class annotationType)
   {
      for (Class businessInterface: getBusinessInterfaces() )
      {
         if ( hasAnnotation(businessInterface, annotationType) )
         {
            return true;
         }
      }
      return false;
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

   public Collection<Method> getRemoveMethods()
   {
      return removeMethods.values();
   }

   public Method getRemoveMethod(String name)
   {
      return removeMethods.get(name);
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

   public List<BijectedAttribute<Out>> getOutAttributes()
   {
      return outAttributes;
   }

   public List<BijectedAttribute<In>> getInAttributes()
   {
      return inAttributes;
   }

    public boolean needsInjection() 
    {
        return 
            !getInAttributes().isEmpty() ||
            !dataModelSelectionSetters.isEmpty() ||
            !parameterSetters.isEmpty();
    }

    public boolean needsOutjection() 
    {
        return 
            !getOutAttributes().isEmpty() ||
            !dataModelGetters.isEmpty();
    }

    protected Object instantiate() throws Exception
    {
        switch(type) {
           case JAVA_BEAN:
              return instantiateJavaBean();
           case ENTITY_BEAN:
              return instantiateEntityBean();
           case STATELESS_SESSION_BEAN:
           case STATEFUL_SESSION_BEAN:
              return instantiateSessionBean();
           case MESSAGE_DRIVEN_BEAN:
              throw new UnsupportedOperationException("Message-driven beans may not be called: " + name);
           default:
              throw new IllegalStateException();
        }
    }

   protected Object instantiateSessionBean() throws Exception, NamingException
   {
      Component old = SeamInterceptor.COMPONENT.get();
      SeamInterceptor.COMPONENT.set(this);
      try
      {
         Object bean = Naming.getInitialContext().lookup(jndiName);
         return wrap( bean, new ClientSideInterceptor(bean, this) );
      }
      finally
      {
         SeamInterceptor.COMPONENT.set(old);
      }
   }

   protected Object instantiateEntityBean() throws Exception
   {
      Object bean = beanClass.newInstance();
      initialize(bean);
      return bean;
   }

   protected Object instantiateJavaBean() throws Exception
   {
      Object bean = beanClass.newInstance();
      if (interceptionType==InterceptionType.NEVER)
      {
         initialize(bean);
         callPostConstructMethod(bean);
      }
      else
      {
         JavaBeanInterceptor interceptor = new JavaBeanInterceptor(bean, this);
         bean = wrap(bean, interceptor);
         interceptor.postConstruct();
      }
      return bean;
   }

   /**
    * Wrap a CGLIB interceptor around an instance of the component
    */
   public Object wrap(Object bean, MethodInterceptor interceptor) throws Exception
   {
      Factory proxy = getProxyFactory().newInstance();
      proxy.setCallback(0, interceptor);
      return proxy;
   }

   private synchronized Class<Factory> getProxyFactory()
   {
      if (factory==null)
      {
         factory = createProxyFactory( getType(), getBeanClass(), getBusinessInterfaces() );
      }
      return factory;
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

      if ( log.isDebugEnabled() ) log.debug("done initializing: " + name);
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
      if ( log.isTraceEnabled() )
      {
         log.trace("injecting dependencies of: " + getName());
      }
      //injectLog(bean);
      injectAttributes(bean, enforceRequired);
      injectDataModelSelections(bean);
      injectParameters(bean);
   }

   /**
    * Null out any @In attributes of a component instance.
    *
    * @param bean a Seam component instance
    */
   public void disinject(Object bean)
   {
      if ( log.isTraceEnabled() )
      {
         log.trace("disinjecting dependencies of: " + getName());
      }
      disinjectAttributes(bean);
   }

   private void injectLog(Object bean)
   {
      for (int i=0; i<logFields.size(); i++)
      {
         setFieldValue( bean, logFields.get(i), "log", logInstances.get(i) );
      }
   }

   private void injectParameters(Object bean)
   {
      Map<String, String[]> requestParameters = Parameters.getRequestParameters();

      for (BijectedAttribute setter: parameterSetters)
      {
         Object convertedValue = Parameters.convertMultiValueRequestParameter(requestParameters, setter.getName(), setter.getType());
         setter.set(bean, convertedValue);
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
      if ( log.isTraceEnabled() )
      {
         log.trace("outjecting dependencies of: " + getName());
      }
      outjectAttributes(bean, enforceRequired);
      outjectDataModels(bean);
   }

   private void injectDataModelSelections(Object bean)
   {
      for ( BijectedAttribute dataModelGetter: dataModelGetters )
      {
         injectDataModelSelection(bean, dataModelGetter);
      }
   }

   private void injectDataModelSelection(Object bean, BijectedAttribute dataModelGetter)
   {
      DataBinder wrapper = createWrapper( dataModelGetter.getAnnotation() );
      String name = dataModelGetter.getName();
      Annotation dataModelAnn = dataModelGetter.getAnnotation();
      ScopeType scope = wrapper.getVariableScope(dataModelAnn);
      
      Object dataModel = getOutScope(scope, this).getContext().get(name);
      if ( dataModel!=null )
      {
      
         if (null!=null)
         {
            setFieldValue( bean, null, name, wrapper.getWrappedData(dataModelAnn, dataModel) ); //for PAGE scope datamodels (does not work for properties!)
         }
      
         Object selectedIndex = wrapper.getSelection(dataModelAnn, dataModel);
      
         if ( log.isDebugEnabled() ) log.debug( "selected row: " + selectedIndex );
      
         if ( selectedIndex!=null )
         {
            BijectedAttribute setter = dataModelSelectionSetters.get(name);
            if (setter != null)
            {
               Annotation dataModelSelectionAnn = setter.getAnnotation();
               Object selection = createUnwrapper(dataModelSelectionAnn).getSelection(dataModelSelectionAnn, dataModel);
               setter.set(bean, selection);
            }
         }
      
      }
   }

   private void outjectDataModels(Object bean)
   {
      for ( BijectedAttribute dataModelGetter: dataModelGetters )
      {
         outjectDataModel(bean, dataModelGetter);
      }
   }

   private void outjectDataModel(Object bean, BijectedAttribute dataModelGetter)
   {
      DataBinder wrapper = createWrapper( dataModelGetter.getAnnotation() );
      Object list = dataModelGetter.get(bean);
      String name = dataModelGetter.getName();
      Annotation dataModelAnn = dataModelGetter.getAnnotation();
      ScopeType scope = wrapper.getVariableScope(dataModelAnn);
      
      Context context = getOutScope(scope, this).getContext();
      Object existingDataModel = context.get(name);
      boolean dirty = existingDataModel == null || scope==PAGE ||
            wrapper.isDirty(dataModelAnn, existingDataModel, list);
      
      if (dirty)
      {
         if ( list!=null )
         {
            context.set( name, wrapper.wrap(dataModelAnn, list) );
         }
         else
         {
            context.remove(name);
         }
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

   private static ScopeType getOutScope(ScopeType specifiedScope, Component component)
   {
      ScopeType scope = component==null ? EVENT : component.getScope();
      if (scope==STATELESS)
      {
         scope = EVENT;
      }
      if (specifiedScope!=UNSPECIFIED)
      {
         scope = specifiedScope;
      }
      return scope;
   }

   private void injectAttributes(Object bean, boolean enforceRequired)
   {
      for ( BijectedAttribute<In> att : getInAttributes() )
      {
         att.set( bean, getValueToInject( att.getAnnotation(), att.getName(), bean, enforceRequired ) );
      }
   }

   private void disinjectAttributes(Object bean)
   {
      for ( BijectedAttribute att: getInAttributes() )
      {
         if ( !att.getType().isPrimitive() )
         {
            att.set(bean, null);
         }
      }
   }

   private void outjectAttributes(Object bean, boolean enforceRequired)
   {
      for ( BijectedAttribute<Out> att: getOutAttributes() )
      {
         outjectAttribute( att.getAnnotation(), att.getName(), bean, att.get(bean), enforceRequired );
      }
   }

   private void outjectAttribute(Out out, String name, Object bean, Object value, boolean enforceRequired)
   {
      
      if (value==null && enforceRequired && out.required())
      {
         throw new RequiredException(
               "@Out attribute requires non-null value: " +
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
                        "attempted to bind an @Out attribute of the wrong type to: " +
                        getAttributeMessage(name)
                     );
               }
            }
         }
         else if ( out.scope()==STATELESS )
         {
            throw new IllegalArgumentException(
                  "cannot specify explicit scope=STATELESS on @Out: " +
                  getAttributeMessage(name)
               );
         }
      
         ScopeType outScope = component==null ?
               getOutScope( out.scope(), this ) :
               component.getScope();
      
         if ( enforceRequired || outScope.isContextActive() )
         {
            if (value==null)
            {
               outScope.getContext().remove(name);
            }
            else
            {
               outScope.getContext().set(name, value);
            }
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
            for ( Class businessInterface: businessInterfaces )
            {
               if ( businessInterface.isAssignableFrom(clazz) )
               {
                  return true;
               }
            }
            return false;
      }
   }

   public static Set<Class> getBusinessInterfaces(Class clazz)
   {
      Set<Class> result = new HashSet<Class>();

      if ( clazz.isAnnotationPresent(LOCAL) )
      {
         for ( Class iface: value( clazz.getAnnotation(LOCAL) ) ) {
            result.add(iface);
         }
      }

      if ( clazz.isAnnotationPresent(REMOTE) )
      {
         for ( Class iface: value( clazz.getAnnotation(REMOTE) ) )
         {
            result.add(iface);
         }
      }

      for ( Class iface: clazz.getInterfaces() )
      {
         if ( iface.isAnnotationPresent(LOCAL) || iface.isAnnotationPresent(REMOTE) )
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
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No application context active");
      }
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

   public static Object getInstance(Class<?> clazz, ScopeType scope)
   {
      return getInstance(clazz, scope, true);
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

   public static Object getInstance(String name, ScopeType scope)
   {
      return getInstance(name, scope, true);
   }

   public static Object getInstance(String name, ScopeType scope, boolean create)
   {
      Object result = scope==STATELESS ? null : scope.getContext().get(name);
      result = getInstance(name, create, result);
      return result;
   }

   private static Object getInstance(String name, boolean create, Object result) {
      Component component = Component.forName(name);

      create = create || Init.instance().isAutocreateVariable(name);

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
           else if ( component.getScope().isContextActive() )
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
               if ( component.hasUnwrapMethod() ) return result; ///best way???
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
         Init.FactoryBinding methodBinding = init.getFactoryMethodBinding(name);
         Init.FactoryBinding valueBinding = init.getFactoryValueBinding(name);
         if ( methodBinding!=null && getOutScope( methodBinding.getScope(), null ).isContextActive() ) //let the XML take precedence
         {
            Object result = methodBinding.getMethodBinding().invoke();
            return handleFactoryMethodResult( name, null, result, methodBinding.getScope() );
         }
         else if ( valueBinding!=null && getOutScope( valueBinding.getScope(), null ).isContextActive() ) //let the XML take precedence
         {
            Object result = valueBinding.getValueBinding().getValue();
            return handleFactoryMethodResult( name, null, result, valueBinding.getScope() );
         }
         else if ( factoryMethod!=null && getOutScope( factoryMethod.getScope(), factoryMethod.getComponent() ).isContextActive() )
         {
            Object factory = Component.getInstance( factoryMethod.getComponent().getName(), true );
            if (factory==null)
            {
               return null;
            }
            else
            {
               Object result = factoryMethod.getComponent().callComponentMethod( factory, factoryMethod.getMethod() );
               return handleFactoryMethodResult( name, factoryMethod.getComponent(), result, factoryMethod.getScope() );
            }
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
         ScopeType outScope = getOutScope(scope, component);
         if ( outScope!=STATELESS )
         {
            outScope.getContext().set(name, result);
         }
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

   public long getTimeout()
   {
      return timeout;
   }

   public Object callComponentMethod(Object instance, Method method, Object... parameters) {
      Class[] paramTypes = method.getParameterTypes();
      String methodName = method.getName();
      try
      {
         Method interfaceMethod = instance.getClass().getMethod(methodName, paramTypes);
         if ( paramTypes.length==0 || interfaceMethod.getParameterTypes().length==0 )
         {
            return Reflections.invokeAndWrap(interfaceMethod, instance);
         }
         else if ( parameters.length>0 )
         {
            return Reflections.invokeAndWrap(interfaceMethod, instance, parameters);
         }
         else
         {
            return Reflections.invokeAndWrap(interfaceMethod, instance, this);
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

   private Object getValueToInject(In in, String name, Object bean, boolean enforceRequired)
   {
      Object result;
      if ( name.startsWith("#") )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject with EL expression: " + name);
         }
         result = Expressions.instance().createValueBinding(name).getValue();
      }
      else if ( in.scope()==UNSPECIFIED )
      {
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject with hierarchical context search: " + name);
         }
         result = getInstance( name, in.create() && !org.jboss.seam.contexts.Lifecycle.isDestroying() );
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
         if ( in.scope()==STATELESS )
         {
            throw new IllegalArgumentException(
                  "cannot specify explicit scope=STATELESS on @In: " +
                  getAttributeMessage(name)
               );
         }
         if ( log.isDebugEnabled() )
         {
            log.debug("trying to inject from specified context: " + name + ", scope: " + scope);
         }
         if ( enforceRequired || in.scope().isContextActive() )
         {
            result = in.scope().getContext().get(name);
         }
         else
         {
            return null;
         }
      }

      if ( result==null && enforceRequired && in.required() )
      {
         throw new RequiredException(
               "In attribute requires non-null value: " +
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

   @Override
   public String toString()
   {
      return "Component(" + name + ")";
   }

   public static Class<Factory> createProxyFactory(ComponentType type, final Class beanClass, Collection<Class> businessInterfaces)
   {
      Enhancer en = new Enhancer();
      en.setUseCache(false);
      en.setInterceptDuringConstruction(false);
      en.setCallbackType(MethodInterceptor.class);
      en.setSuperclass( type==JAVA_BEAN ? beanClass : Object.class );
      /*en.setNamingPolicy( new NamingPolicy() {
         public String getClassName(String prefix, String source, Object key, Predicate names)
         {
            return beanClass.getName() + "SeamProxy";
         }
      });*/
      Set<Class> interfaces = new HashSet<Class>();
      if ( type.isSessionBean() )
      {
         interfaces.addAll(businessInterfaces);
      }
      else
      {
         interfaces.add(HttpSessionActivationListener.class);
         interfaces.add(Mutable.class);
      }
      interfaces.add(Proxy.class);
      en.setInterfaces( interfaces.toArray( new Class[0] ) );
      return en.createClass();
   }

   public InterceptionType getInterceptionType()
   {
      return interceptionType;
   }

   public boolean isStartup() 
   {
      return startup;
   }

   public String[] getDependencies()
   {
      return dependencies;
   }

   public boolean isLifecycleMethod(Method method)
   {
      return method==null || //EJB 3 JavaDoc says InvocationContext.getMethod() returns null for lifecycle callbacks!
            lifecycleMethods.contains(method);
   }
   
   public boolean isConversationManagementMethod(Method method)
   {
      return method!=null && 
            conversationManagementMethods.contains(method);
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

      @Override
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
            value = createValueBinding().getValue();
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
         return Expressions.instance().createValueBinding(expression);
      }

      private MethodBinding createMethodBinding()
      {
         return Expressions.instance().createMethodBinding(expression);
      }

      @Override
      public String toString()
      {
         return "ELInitialValue(" + expression + ")";
      }

   }

   public static class ListInitialValue implements InitialValue
   {
      private InitialValue[] initialValues;
      private Class elementType;
      private boolean isArray;

      public ListInitialValue(PropertyValue propertyValue, Class collectionClass, Type collectionType)
      {
         String[] expressions = propertyValue.getMultiValues();
         initialValues = new InitialValue[expressions.length];
         isArray = collectionClass.isArray();
         elementType = isArray ? 
                  collectionClass.getComponentType() : 
                  Reflections.getCollectionElementType(collectionType);
         for ( int i=0; i<expressions.length; i++ )
         {
            PropertyValue elementValue = new Conversions.FlatPropertyValue( expressions[i] );
            initialValues[i] = getInitialValue(elementValue, elementType, elementType);
         }
      }

      public Object getValue(Class type)
      {
         if (isArray)
         {
            Object array = Array.newInstance(elementType, initialValues.length);
            for (int i=0; i<initialValues.length; i++)
            {
               Array.set( array, i, initialValues[i].getValue(elementType) );
            }
            return array;
         }
         else
         {
            List list = new ArrayList(initialValues.length);
            for (InitialValue iv: initialValues)
            {
               list.add( iv.getValue(elementType) );
            }
            return list;
         }
      }
      
      @Override
      public String toString()
      {
         return "ListInitialValue(" + elementType.getSimpleName() + ")";
      }

   }
   
   public static class MapInitialValue implements InitialValue
   {
      private Map<InitialValue, InitialValue> initialValues;
      private Class elementType;
      private Class keyType;

      public MapInitialValue(PropertyValue propertyValue, Class collectionClass, Type collectionType)
      {
         Map<String, String> expressions = propertyValue.getKeyedValues();
         initialValues = new HashMap<InitialValue, InitialValue>(expressions.size());
         elementType = Reflections.getCollectionElementType(collectionType);
         keyType = Reflections.getMapKeyType(collectionType);
         for ( Map.Entry<String, String> me: expressions.entrySet() )
         {
            PropertyValue keyValue = new Conversions.FlatPropertyValue( me.getKey() );
            PropertyValue elementValue = new Conversions.FlatPropertyValue( me.getValue() );
            initialValues.put( getInitialValue(keyValue, keyType, keyType), getInitialValue(elementValue, elementType, elementType) ); 
         }
      }

      public Object getValue(Class type)
      {
         Map result = new HashMap(initialValues.size());
         for ( Map.Entry<InitialValue, InitialValue> me : initialValues.entrySet() )
         {
            result.put( me.getKey().getValue(keyType), me.getValue().getValue(elementType) );
         }
         return result;
      }
      
      @Override
      public String toString()
      {
         return "MapInitialValue(" + keyType.getSimpleName() + "," + elementType.getSimpleName() + ")";
      }

   }
   
   public interface BijectedAttribute<T extends Annotation>
   {
      public String getName();
      public T getAnnotation();
      public Class getType();
      public void set(Object bean, Object value);
      public Object get(Object bean);
   }

   public final class BijectedMethod<T extends Annotation> implements BijectedAttribute<T>
   {
      private final String name;
      private final Method method;
      private final T annotation;
      
      private BijectedMethod(String name, Method method, T annotation)
      {
         this.name = name;
         this.method = method;
         this.annotation = annotation;
      }
      public String getName()
      {
         return name;
      }
      public Method getMethod()
      {
         return method;
      }
      public T getAnnotation()
      {
         return annotation;
      }
      public void set(Object bean, Object value)
      {
         setPropertyValue(bean, method, name, value);
      }
      public Object get(Object bean)
      {
         return getPropertyValue(bean, method, name);
      }
      public Class getType()
      {
         return method.getParameterTypes()[0];
      }
      @Override
      public String toString()
      {
         return "BijectedMethod(" + name + ')';
      }
   }
   
   public final class BijectedField<T extends Annotation> implements BijectedAttribute<T>
   {
      private final String name;
      private final Field field;
      private final T annotation;
      
      private BijectedField(String name, Field field, T annotation)
      {
         this.name = name;
         this.field = field;
         this.annotation = annotation;
      }
      public String getName()
      {
         return name;
      }
      public Field getField()
      {
         return field;
      }
      public T getAnnotation()
      {
         return annotation;
      }
      public Class getType()
      {
         return field.getType();
      }
      public void set(Object bean, Object value)
      {
         setFieldValue(bean, field, name, value);
      }
      public Object get(Object bean)
      {
         return getFieldValue(bean, field, name);
      }
      @Override
      public String toString()
      {
         return "BijectedField(" + name + ')';
      }
   }
   
}
