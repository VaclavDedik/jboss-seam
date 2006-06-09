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
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remove;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.interceptor.Interceptors;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.ClassValidator;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.DataBinderClass;
import org.jboss.seam.annotations.DataSelectorClass;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.databinding.DataBinder;
import org.jboss.seam.databinding.DataSelector;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.Interceptor;
import org.jboss.seam.interceptors.JavaBeanInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.RollbackInterceptor;
import org.jboss.seam.interceptors.TransactionInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.SortItem;
import org.jboss.seam.util.SorterNew;
import org.jboss.seam.util.StringArrayPropertyEditor;
import org.jboss.seam.util.SetPropertyEditor;

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

   //static
   {
      PropertyEditorManager.registerEditor(String[].class, StringArrayPropertyEditor.class);
      PropertyEditorManager.registerEditor(Set.class, SetPropertyEditor.class);
   }

   private static final Log log = LogFactory.getLog(Component.class);

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
   private Set<Field> parameterFields = new HashSet<Field>();
   private Set<Method> parameterSetters = new HashSet<Method>();
   private Map<Method, Object> initializers = new HashMap<Method, Object>();

   private List<Method> dataModelGetters = new ArrayList<Method>();
   private Map<Method, Annotation> dataModelGetterAnnotations = new HashMap<Method, Annotation>();
   private Map<String, Method> dataModelSelectionSetters = new HashMap<String, Method>();
   private Map<Method, Annotation> dataModelSelectionSetterAnnotations = new HashMap<Method, Annotation>();
   private List<Field> dataModelFields = new ArrayList<Field>();
   private Map<Field, Annotation> dataModelFieldAnnotations = new HashMap<Field, Annotation>();
   private Map<String, Field> dataModelSelectionFields = new HashMap<String, Field>();
   private Map<Field, Annotation> dataModelSelectionFieldAnnotations = new HashMap<Field, Annotation>();

   private Hashtable<Locale, ClassValidator> validators = new Hashtable<Locale, ClassValidator>();

   private List<Interceptor> interceptors = new ArrayList<Interceptor>();

   private Set<Class> localInterfaces;

   private Class<Factory> factory;

   public Component(Class<?> clazz)
   {
      this( clazz, Seam.getComponentName(clazz) );
   }

   public Component(Class<?> clazz, String componentName)
   {
      this(clazz, componentName, Seam.getComponentScope(clazz));
   }

   public Component(Class<?> clazz, String componentName, ScopeType componentScope)
   {
      this(clazz, componentName, componentScope, Contexts.getApplicationContext());
   }

   public Component(Class<?> clazz, Context applicationContext)
   {
      this( clazz, Seam.getComponentName(clazz), Seam.getComponentScope(clazz), applicationContext );
   }

   public Component(Class<?> clazz, String componentName, ScopeType componentScope, Context applicationContext)
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
         dependencies = getBeanClass().getAnnotation(Startup.class).depends();
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

      localInterfaces = getLocalInterfaces(beanClass);

      if ( interceptionType!=InterceptionType.NEVER)
      {
         initInterceptors();
      }

      initInitializers(applicationContext);

      if (type==ComponentType.JAVA_BEAN)
      {
         factory = createProxyFactory();
      }

   }

   private void checkScopeForComponentType()
   {
      if ( scope==ScopeType.STATELESS && (type==ComponentType.STATEFUL_SESSION_BEAN || type==ComponentType.ENTITY_BEAN) )
      {
         throw new IllegalArgumentException("Only stateless session beans and Java beans may be bound to the STATELESS context: " + name);
      }
      if ( scope==ScopeType.PAGE && type==ComponentType.STATEFUL_SESSION_BEAN )
      {
         throw new IllegalArgumentException("Stateful session beans may not be bound to the PAGE context: " + name);
      }
      if ( scope==ScopeType.APPLICATION && type==ComponentType.STATEFUL_SESSION_BEAN )
      {
         log.warn("Stateful session beans was bound to the APPLICATION context - note that it is not safe to make concurrent calls to the bean: " + name);
      }
      if ( scope!=ScopeType.STATELESS && type==ComponentType.MESSAGE_DRIVEN_BEAN )
      {
         throw new IllegalArgumentException("Message-driven beans must be bound to STATELESS context: " + name);
      }

      boolean serializableScope = scope==ScopeType.PAGE || scope==ScopeType.SESSION || scope==ScopeType.CONVERSATION;
      boolean serializableType = type==ComponentType.JAVA_BEAN || type==ComponentType.ENTITY_BEAN;
      if ( serializableType && serializableScope && !Serializable.class.isAssignableFrom(beanClass) )
      {
         log.warn("Component class should be serializable: " + name);
      }
   }

   private void checkDestroyMethod()
   {
      if ( type==ComponentType.STATEFUL_SESSION_BEAN && ( destroyMethod==null || !removeMethods.contains(destroyMethod) ) )
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
      Map<String, String> properties = (Map<String, String>) applicationContext.get(PROPERTIES);
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
               throw new IllegalArgumentException("no property for configuration setting: " + key, ie);
            }
            PropertyEditor propertyEditor = PropertyEditorManager.findEditor( propertyDescriptor.getPropertyType() );
            propertyEditor.setAsText( value );
            initializers.put( propertyDescriptor.getWriteMethod(), propertyEditor.getValue() );
            log.debug( key + "=" + value );
        }

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
               if (destroyMethod!=null)
               {
                  throw new IllegalStateException("component has two @Destroy methods: " + name);
               }
               destroyMethod = method;
            }
            if ( method.isAnnotationPresent(Create.class) )
            {
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
      if ( dataModelScope!=ScopeType.PAGE && dataModelScope!=ScopeType.UNSPECIFIED )
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
            interceptors.add( new Interceptor(annotation, this) );
         }
      }

      newSort(interceptors);

      log.trace("interceptor stack: " + interceptors);
   }

   private List<Interceptor> newSort(List<Interceptor> list)
   {
      List<SortItem<Interceptor>> siList = new ArrayList<SortItem<Interceptor>>();
      Map<Class<?>,SortItem<Interceptor>> ht = new HashMap<Class<?>,SortItem<Interceptor>>();

      for (Interceptor i : list)
      {
         SortItem<Interceptor> si = new SortItem<Interceptor>(i);
         siList.add(si);
         ht.put( i.getUserInterceptor().getClass(), si );
      }

      for (SortItem<Interceptor> si : siList)
      {
         Class<?> clazz = si.getObj().getUserInterceptor().getClass();
         Around around = clazz.getAnnotation(Around.class);
         Within within = clazz.getAnnotation(Within.class);
         if  (around!=null)
         {
            for (Class<?> cl : Arrays.asList( around.value() ) )
            {
               si.getAround().add( ht.get(cl) );
            }
         }
         if (within!=null)
         {
            for (Class<?> cl : Arrays.asList( within.value() ) )
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
      interceptors.add( new Interceptor( new OutcomeInterceptor(), this ) );
      interceptors.add( new Interceptor( new RemoveInterceptor(), this ) );
      interceptors.add( new Interceptor( new BusinessProcessInterceptor(), this ) );
      interceptors.add( new Interceptor( new ConversationInterceptor(), this ) );
      interceptors.add( new Interceptor( new BijectionInterceptor(), this ) );
      interceptors.add( new Interceptor( new ValidationInterceptor(), this ) );
      interceptors.add( new Interceptor( new RollbackInterceptor(), this ) );
      if ( getType()==ComponentType.JAVA_BEAN )
      {
         interceptors.add( new Interceptor( new TransactionInterceptor(), this ) );
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
      log.debug("instantiating Seam component: " + name);

      try
      {
         return initialize( instantiate() );
      }
      catch (Exception e)
      {
         throw new InstantiationException("Could not instantiate Seam component: " + name, e);
      }
   }

    public boolean needsInjection() {
        return !getInFields().isEmpty() ||
            !getInMethods().isEmpty() ||
            !dataModelSelectionSetters.isEmpty() ||
            !dataModelSelectionFields.isEmpty() ||
            !parameterFields.isEmpty() ||
            !parameterSetters.isEmpty();
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
           case STATELESS_SESSION_BEAN:
           case STATEFUL_SESSION_BEAN:
           case MESSAGE_DRIVEN_BEAN:
              return Naming.getInitialContext().lookup(jndiName);
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
      injectDataModelSelection(bean);
      injectParameters(bean);
   }

   private void injectParameters(Object bean)
   {
      Map requestParameters = null;
      if ( FacesContext.getCurrentInstance() != null )
      {
         requestParameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
      }
      else if ( Lifecycle.getServletRequest() != null )
      {
         requestParameters = Lifecycle.getServletRequest().getParameterMap();
      }

      for (Method setter: parameterSetters)
      {
         String name = toName( setter.getAnnotation(RequestParameter.class).value(), setter );
         Object convertedValue = convertRequestParameter( requestParameters.get(name), setter.getParameterTypes()[0] );
         setPropertyValue( bean, setter, name, convertedValue );
      }
      for (Field field: parameterFields)
      {
         String name = toName( field.getAnnotation(RequestParameter.class).value(), field );
         Object convertedValue = convertRequestParameter( requestParameters.get(name), field.getType() );
         setFieldValue( bean, field, name, convertedValue );
      }
   }

   private Object convertRequestParameter(Object requestParameter, Class type)
   {
      if ( String.class.equals(type) ) return requestParameter;

      FacesContext facesContext = FacesContext.getCurrentInstance();
      Converter converter = facesContext.getApplication().createConverter(type);
      if (converter==null)
      {
         throw new IllegalArgumentException("no converter for type: " + type);
      }
      return converter.getAsObject( facesContext, facesContext.getViewRoot(), (String) requestParameter );
   }

   public void outject(Object bean)
   {
      outjectMethods(bean);
      outjectFields(bean);
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

      Object dataModel = getDataModelContext(scope).get(name);
      if ( dataModel != null )
      {

         if (dataModelField!=null)
         {
            setFieldValue( bean, dataModelField, name, wrapper.getWrappedData(dataModelAnn, dataModel) ); //for PAGE scope datamodels (does not work for properties!)
         }

         Object selectedIndex = wrapper.getSelection(dataModelAnn, dataModel);

         log.debug( "selected row: " + selectedIndex );

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

      Context context = getDataModelContext(scope);
      Object existingDataModel = context.get(name);
      boolean dirty = existingDataModel == null || scope==ScopeType.PAGE ||
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

   private Context getDataModelContext(ScopeType specifiedScope) {
      ScopeType scope = this.scope;
      if (scope==ScopeType.STATELESS)
      {
         scope = ScopeType.EVENT;
      }
      if (specifiedScope!=ScopeType.UNSPECIFIED)
      {
         scope = specifiedScope;
      }
      return scope.getContext();
   }

   private void injectMethods(Object bean/*, boolean isActionInvocation*/)
   {
      for (Method method : getInMethods())
      {
         In in = method.getAnnotation(In.class);
         //if ( isActionInvocation || in.alwaysDefined() )
         //{
            String name = toName(in.value(), method);
            setPropertyValue( bean, method, name, getInstanceToInject(in, name, bean) );
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
            setFieldValue( bean, field, name, getInstanceToInject(in, name, bean) );
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
            String name = toName(out.value(), field);
            setOutjectedValue( out, name, getFieldValue(bean, field, name) );
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
            String name = toName(out.value(), method);
            setOutjectedValue( out, name, getPropertyValue(bean, method, name) );
         }
      }
   }

   private void setOutjectedValue(Out out, String name, Object value)
   {
      if (value==null && out.required())
      {
         throw new RequiredException(
               "Out attribute requires value for component: " +
               getAttributeMessage(name)
            );
      }
      else
      {
         ScopeType scope;
         if (out.scope()==ScopeType.UNSPECIFIED)
         {
            Component component = Component.forName(name);
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
            scope = component==null ? ScopeType.EVENT : component.getScope();
         }
         else
         {
            scope = out.scope();
         }
         if (value==null)
         {
            scope.getContext().remove(name);
         }
         else
         {
            scope.getContext().set(name, value);
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

   public Set<Class> getLocalInterfaces()
   {
     return getLocalInterfaces(beanClass);
   }

    private static boolean isExcludedLocalInterfaceName(String name) {
        return name.equals("java.io.Serializable") ||
               name.equals("java.io.Externalizable") ||
               name.startsWith("javax.ejb.");
    }



   private Object getFieldValue(Object bean, Field field, String name)
   {
      try {
         return field.get(bean);
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
         field.set(bean, value);
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
      if (result == null && create)
      {
        result = getInstanceFromFactory(name);
        if (result==null)
        {
             result = newInstance(name);
        }
      }
      if (result!=null)
      {
         Component component = Component.forName(name);
         if (component!=null)
         {
            if ( !component.isInstance(result) )
            {
               throw new IllegalArgumentException( "value found for In attribute has the wrong type: " + name );
            }
         }
         result = unwrap( component, result );
      }
      return result;
   }

   public static Object getInstanceFromFactory(String name)
   {
      Init init = Init.instance();
      Init.FactoryMethod factoryMethod = init==null ?
            null : init.getFactory(name);
      if (factoryMethod==null)
      {
         return null;
      }
      else
      {
         Object factory = Component.getInstance( factoryMethod.component.getName(), true );
         callComponentMethod(factoryMethod.component, factory, factoryMethod.method);
         return Contexts.lookupInStatefulContexts(name);
      }
   }

   public static Object newInstance(String name)
   {
      Component component = Component.forName(name);
      if (component == null)
      {
         log.debug("seam component not found: " + name);
         return null; //needed when this method is called by JSF
      }
      else
      {
         Object instance = component.newInstance();
         if (component.getScope()!=ScopeType.STATELESS)
         {
            component.getScope().getContext().set(name, instance); //put it in the context _before_ calling the create method
            callCreateMethod(component, instance);
         }
         return instance;
      }
   }

   private static void callCreateMethod(Component component, Object instance)
   {
      if (component.hasCreateMethod())
      {
         callComponentMethod( component, instance, component.getCreateMethod() );
      }
   }

   public static Object callComponentMethod(Component component, Object instance, Method method) {
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
            return Reflections.invokeAndWrap( interfaceMethod, instance, component );
         }
      }
      catch (NoSuchMethodException e)
      {
         throw new IllegalArgumentException("create method not found", e);
      }
   }

   private static Object unwrap(Component component, Object instance)
   {
      if (component!=null && component.hasUnwrapMethod())
      {
         instance = callComponentMethod(component, instance, component.getUnwrapMethod() );
      }
      return instance;
   }

   private Object getInstanceToInject(In in, String name, Object bean)
   {
      Object result;
      if ( name.startsWith("#") )
      {
         FacesContext facesCtx = FacesContext.getCurrentInstance();
         Application application = facesCtx.getApplication();
         result = application.createValueBinding(name).getValue(facesCtx);
      }
      else if (in.scope()==ScopeType.UNSPECIFIED)
      {
         result = getInstance(name, in.create());
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
         result = in.scope().getContext().get(name);
      }

      if (result==null && in.required())
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
      en.setSuperclass(beanClass);
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

}
