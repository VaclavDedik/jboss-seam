/*
?* JBoss, Home of Professional Open Source
?*
?* Distributable under LGPL license.
?* See terms of license at gnu.org.
?*/
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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.Interceptors;
import javax.ejb.Local;
import javax.ejb.Remove;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.hibernate.validator.ClassValidator;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.JndiName;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.Interceptor;
import org.jboss.seam.interceptors.JavaBeanInterceptor;
import org.jboss.seam.interceptors.OutcomeInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.RollbackInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;
import org.jboss.seam.util.NamingHelper;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Sorter;
import org.jboss.seam.util.StringArrayPropertyEditor;
import org.jboss.seam.util.Strings;

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
   private Set<Field> parameterFields = new HashSet<Field>();
   private Set<Method> parameterSetters = new HashSet<Method>();
   private Map<Method, Object> initializers = new HashMap<Method, Object>();

   private List<Method> dataModelGetters = new ArrayList<Method>();
   private Map<String, Method> dataModelSelectionIndexSetters = new HashMap<String, Method>();
   private Map<String, Method> dataModelSelectionSetters = new HashMap<String, Method>();
   private List<Field> dataModelFields = new ArrayList<Field>();
   private Map<String, Field> dataModelSelectionIndexFields = new HashMap<String, Field>();
   private Map<String, Field> dataModelSelectionFields = new HashMap<String, Field>();

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

   private void checkScopeForComponentType() {
      if ( scope==ScopeType.STATELESS && (type==ComponentType.STATEFUL_SESSION_BEAN || type==ComponentType.ENTITY_BEAN) )
      {
         throw new IllegalArgumentException("Only stateless session beans and Java beans may be bound to the STATELESS context: " + name);
      }
      if ( scope==ScopeType.PAGE && type==ComponentType.STATEFUL_SESSION_BEAN )
      {
         throw new IllegalArgumentException("Stateful session beans may not be bound to the PAGE context: " + name);
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
      List<Method> selectionIndexSetters = new ArrayList<Method>();
      List<Field> selectionFields = new ArrayList<Field>();
      List<Field> selectionIndexFields = new ArrayList<Field>();

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
            if ( method.isAnnotationPresent(DataModel.class) )
            {
               checkDataModelScope( method.getAnnotation(DataModel.class) );
               dataModelGetters.add(method);
            }
            if ( method.isAnnotationPresent(org.jboss.seam.annotations.Factory.class) )
            {
               Init init = (Init) applicationContext.get( Seam.getComponentName(Init.class) );
               init.addFactoryMethod(
            			method.getAnnotation(org.jboss.seam.annotations.Factory.class).value(),
            			method,
            			this
            		);
            }
            if ( method.isAnnotationPresent(DataModelSelectionIndex.class) )
            {
               selectionIndexSetters.add(method);
            }
            if ( method.isAnnotationPresent(DataModelSelection.class) )
            {
               selectionSetters.add(method);
            }
            if ( method.isAnnotationPresent(RequestParameter.class) )
            {
               parameterSetters.add(method);
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
            if ( field.isAnnotationPresent(DataModel.class) )
            {
               checkDataModelScope( field.getAnnotation(DataModel.class) );
               dataModelFields.add(field);
            }
            if ( field.isAnnotationPresent(DataModelSelection.class) )
            {
               selectionFields.add(field);
            }
            if ( field.isAnnotationPresent(DataModelSelectionIndex.class) )
            {
               selectionIndexFields.add(field);
            }
            if ( field.isAnnotationPresent(RequestParameter.class) )
            {
               parameterFields.add(field);
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
            defaultDataModelName = toName( dataModelGetter.getAnnotation(DataModel.class).value(), dataModelGetter );
         }
         else if ( !dataModelFields.isEmpty() )
         {
            Field dataModelField = dataModelFields.get(0);
            defaultDataModelName = toName( dataModelField.getAnnotation(DataModel.class).value(), dataModelField );
         }
      }

      for (Method method : selectionSetters) {
         String name = method.getAnnotation( DataModelSelection.class ).value();
         if ( name.length() == 0 ) {
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
      for (Field field : selectionFields) {
         String name = field.getAnnotation( DataModelSelection.class ).value();
         if ( name.length() == 0 ) {
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

      for (Method method : selectionIndexSetters) {
         String name = method.getAnnotation( DataModelSelectionIndex.class ).value();
         if ( name.length() == 0 ) {
            if ( hasMultipleDataModels )
            {
               throw new IllegalStateException( "Missing value() for @DataModelSelectionIndex with multiple @DataModels" );
            }
            name = defaultDataModelName;
         }
         Method existing = dataModelSelectionIndexSetters.put( name, method );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelectionIndex setters for: " + name);
         }
      }
      for (Field field : selectionIndexFields) {
         String name = field.getAnnotation( DataModelSelectionIndex.class ).value();
         if ( name.length() == 0 ) {
            if ( hasMultipleDataModels )
            {
               throw new IllegalStateException( "Missing value() for @DataModelSelectionIndex with multiple @DataModels" );
            }
            name = defaultDataModelName;
         }
         Field existing = dataModelSelectionIndexFields.put( name, field );
         if (existing!=null)
         {
            throw new IllegalStateException("Multiple @DataModelSelectionIndex fields for: " + name);
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
      interceptors.add( new Interceptor( new RollbackInterceptor(), this ) );
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
      java.util.ResourceBundle currentBundle = ResourceBundle.instance();
      Locale currentLocale = currentBundle==null ? Locale.getDefault() : currentBundle.getLocale();
      ClassValidator validator = validators.get(currentLocale);
      if (validator==null)
      {
         if (currentBundle==null)
         {
            validator = new ClassValidator(beanClass);
         }
         else
         {
            validator = new ClassValidator(beanClass, currentBundle);
         }
         validators.put(currentLocale, validator);
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
         throw new InstantiationException("Could not instantiate Seam component", e);
      }
   }

    public boolean needsInjection() {
        return !getInFields().isEmpty() ||
            !getInMethods().isEmpty() ||
            !dataModelSelectionSetters.isEmpty() ||
            !dataModelSelectionIndexSetters.isEmpty() ||
            !dataModelSelectionFields.isEmpty() ||
            !dataModelSelectionIndexFields.isEmpty() ||
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
           case STATELESS_SESSION_BEAN :
           case STATEFUL_SESSION_BEAN :
              return NamingHelper.getInitialContext().lookup(jndiName);
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
      return facesContext
            .getApplication()
            .createConverter(type)
            .getAsObject( facesContext, facesContext.getViewRoot(), (String) requestParameter );
   }

   public void outject(Object bean)
   {
      outjectMethods(bean);
      outjectFields(bean);
      outjectDataModel(bean);
   }

   private void injectDataModelSelection(Object bean)
   {
      for ( Method dataModelGetter : dataModelGetters )
      {
         DataModel dataModelAnn = dataModelGetter.getAnnotation( DataModel.class );
         final String name = toName( dataModelAnn.value(), dataModelGetter );
         injectDataModelSelection( bean, name, dataModelAnn.scope() );
      }
      for ( Field dataModelGetter : dataModelFields )
      {
         DataModel dataModelAnn = dataModelGetter.getAnnotation( DataModel.class );
         final String name = toName( dataModelAnn.value(), dataModelGetter );
         injectDataModelSelection( bean, name, dataModelAnn.scope() );
      }
   }

   private void injectDataModelSelection(Object bean, String name, ScopeType scope)
   {
      javax.faces.model.DataModel dataModel = (javax.faces.model.DataModel) getDataModelContext(scope).get( name );
      if ( dataModel != null )
      {
         int rowIndex = dataModel.getRowIndex();

         log.debug( "selected row: " + rowIndex );

         if ( rowIndex > -1 )
         {

            Method setter = dataModelSelectionIndexSetters.get(name);
            if (setter != null)
            {
               setPropertyValue( bean, setter, name, rowIndex );
            }
            Field field = dataModelSelectionIndexFields.get(name);
            if (field != null)
            {
               setFieldValue( bean, field, name, rowIndex );
            }

            setter = dataModelSelectionSetters.get(name);
            if (setter != null)
            {
               setPropertyValue( bean, setter, name, getSelectedRowData(dataModel) );
            }
            field = dataModelSelectionFields.get(name);
            if (field != null) {
               setFieldValue( bean, field, name, getSelectedRowData(dataModel) );
            }

         }
      }
   }

   private Object getSelectedRowData(javax.faces.model.DataModel dataModel) {
      return dataModel.getRowCount()==0 || dataModel.getRowIndex() == -1 ? null : dataModel.getRowData();
   }

   private void outjectDataModel(Object bean)
   {
      for ( Method dataModelGetter : dataModelGetters )
      {
         final List list;
         final String name;
         DataModel dataModelAnn = dataModelGetter.getAnnotation( DataModel.class );
         name = toName( dataModelAnn.value(), dataModelGetter );
         list = (List) getPropertyValue( bean, dataModelGetter, name );
         outjectDataModelList( name, list, dataModelAnn.scope() );
      }

      for ( Field dataModelGetter : dataModelFields )
      {
         final List list;
         final String name;
         DataModel dataModelAnn = dataModelGetter.getAnnotation( DataModel.class );
         name = toName( dataModelAnn.value(), dataModelGetter );
         list = (List) getFieldValue( bean, dataModelGetter, name );
         outjectDataModelList( name, list, dataModelAnn.scope() );
      }

   }

   private void outjectDataModelList(String name, List list, ScopeType scope)
   {
      Context context = getDataModelContext(scope);
      javax.faces.model.DataModel existingDataModel = (javax.faces.model.DataModel) context.get( name );
      if ( existingDataModel == null || !existingDataModel.getWrappedData().equals(list) )
      {
         if ( list != null )
         {
            ListDataModel dataModel = new org.jboss.seam.jsf.ListDataModel(list);
            context.set( name, dataModel );
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
         throw new RequiredException( "Out attribute requires value for component: " + getAttributeMessage(name) );
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
                  throw new IllegalArgumentException( "attempted to bind an Out attribute of the wrong type to: " + getAttributeMessage(name) );
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
         throw new IllegalArgumentException("could not outject: " + getAttributeMessage(name), e);
      }
   }

   private Object getPropertyValue(Object bean, Method method, String name)
   {
      try {
         return Reflections.invoke(method, bean);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not outject: " + getAttributeMessage(name), e);
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
         throw new IllegalArgumentException("could not inject: " + getAttributeMessage(name), e);
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
         throw new IllegalArgumentException("could not inject: " + getAttributeMessage(name), e);
      }
   }

   public static Component forName(String name)
   {
      return (Component) Contexts.getApplicationContext().get( name + ".component" );
   }

   public static Object getInstance(Class<?> clazz, boolean create)
   {
      return getInstance( Seam.getComponentName(clazz), create );
   }

   public static Object getInstance(Class<?> clazz, ScopeType scope, boolean create)
   {
      return getInstance( Seam.getComponentName(clazz), scope, create );
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
         if ( log.isTraceEnabled() )
         {
            log.trace( Strings.toString(result) );
         }
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
         callComponentMethod( component, instance, component.getCreateMethod() );
      }
   }

   private static Object callComponentMethod(Component component, Object instance, Method method) {
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
            throw new IllegalArgumentException( "cannot combine create=true with explicit scope on @In: " + getAttributeMessage(name) );
         }
         result = in.scope().getContext().get(name);
      }

      if (result==null && in.required())
      {
         throw new RequiredException( "In attribute requires value for component: " +  getAttributeMessage(name) );
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
