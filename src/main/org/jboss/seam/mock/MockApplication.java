package org.jboss.seam.mock;

import static org.jboss.seam.util.EL.EL_CONTEXT;
import static org.jboss.seam.util.EL.EXPRESSION_FACTORY;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.BigDecimalConverter;
import javax.faces.convert.BigIntegerConverter;
import javax.faces.convert.BooleanConverter;
import javax.faces.convert.ByteConverter;
import javax.faces.convert.CharacterConverter;
import javax.faces.convert.Converter;
import javax.faces.convert.DoubleConverter;
import javax.faces.convert.FloatConverter;
import javax.faces.convert.IntegerConverter;
import javax.faces.convert.LongConverter;
import javax.faces.convert.ShortConverter;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import org.jboss.seam.util.Reflections;

public class MockApplication extends Application {

   @Override
   public ActionListener getActionListener() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setActionListener(ActionListener listener) {
      throw new UnsupportedOperationException();
   }
   
   private Locale defaultLocale = Locale.ENGLISH;

   @Override
   public Locale getDefaultLocale() {
      return defaultLocale;
   }

   @Override
   public void setDefaultLocale(Locale locale) {
      defaultLocale = locale;
   }

   @Override
   public String getDefaultRenderKitId() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setDefaultRenderKitId(String rk) {
      throw new UnsupportedOperationException();
   }
   
   private String msgBundleName;

   @Override
   public String getMessageBundle() {
      return msgBundleName;
   }

   @Override
   public void setMessageBundle(String bundleName) {
      this.msgBundleName = bundleName;
   }
   
   private NavigationHandler navigationHandler = new MockNavigationHandler();

   @Override
   public NavigationHandler getNavigationHandler() {
      return navigationHandler;
   }

   @Override
   public void setNavigationHandler(NavigationHandler navigationHandler) {
      this.navigationHandler = navigationHandler;
   }

   @Override
   public PropertyResolver getPropertyResolver() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setPropertyResolver(PropertyResolver pr) {
      throw new UnsupportedOperationException();
   }
   
   private VariableResolver variableResolver = null; //TODO: big big todo!!!!!!!!!!

   @Override
   public VariableResolver getVariableResolver() {
      return variableResolver;
   }

   @Override
   public void setVariableResolver(VariableResolver variableResolver) {
      this.variableResolver = variableResolver;
   }
   
   private ViewHandler viewHandler = new MockViewHandler();

   @Override
   public ViewHandler getViewHandler() {
      return viewHandler;
   }

   @Override
   public void setViewHandler(ViewHandler viewHandler) {
      this.viewHandler = viewHandler;
   }
   
   private StateManager stateManager = new MockStateManager();

   @Override
   public StateManager getStateManager() {
      return stateManager;
   }

   @Override
   public void setStateManager(StateManager stateManager) {
      this.stateManager = stateManager;
   }

   @Override
   public void addComponent(String name, String x) {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIComponent createComponent(String name) throws FacesException {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIComponent createComponent(ValueBinding vb, FacesContext fc,
         String x) throws FacesException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator getComponentTypes() {
      throw new UnsupportedOperationException();
   }
   
   private final Map<Class, Converter> converters = new HashMap<Class, Converter>();
   {
      converters.put(Integer.class, new IntegerConverter());
      converters.put(Long.class, new LongConverter());
      converters.put(Float.class, new FloatConverter());
      converters.put(Double.class, new DoubleConverter());
      converters.put(Boolean.class, new BooleanConverter());
      converters.put(Short.class, new ShortConverter());
      converters.put(Byte.class, new ByteConverter());
      converters.put(Character.class, new CharacterConverter());
      converters.put(BigDecimal.class, new BigDecimalConverter());
      converters.put(BigInteger.class, new BigIntegerConverter());
   }

   @Override
   public void addConverter(String id, String converterClass) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addConverter(Class type, String converterClass) {
      try
      {
         converters.put( type, (Converter) Reflections.classForName(converterClass).newInstance() );
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Converter createConverter(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Converter createConverter(Class clazz) {
      return converters.get(clazz);
   }

   @Override
   public Iterator getConverterIds() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator getConverterTypes() {
      return converters.keySet().iterator();
   }

   @Override
   public MethodBinding createMethodBinding(final String methodExpression, final Class[] args)
         throws ReferenceSyntaxException {
      return new MethodBinding() {
         
         private MethodExpression me = EXPRESSION_FACTORY.createMethodExpression(EL_CONTEXT, methodExpression, Object.class, args);

         @Override
         public String getExpressionString()
         {
            return methodExpression;
         }

         @Override
         public Class getType(FacesContext ctx) throws MethodNotFoundException
         {
            return me.getMethodInfo(EL_CONTEXT).getReturnType();
         }

         @Override
         public Object invoke(FacesContext ctx, Object[] args) throws EvaluationException, MethodNotFoundException
         {
            return me.invoke(EL_CONTEXT, args);
         }
         
      };
   }

   @Override
   public Iterator getSupportedLocales() {
      return Collections.singleton(defaultLocale).iterator();
   }

   @Override
   public void setSupportedLocales(Collection locales) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addValidator(String id, String validator) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Validator createValidator(String id) throws FacesException {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator getValidatorIds() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ValueBinding createValueBinding(final String valueExpression)
         throws ReferenceSyntaxException {
      return new ValueBinding() {
         
         private ValueExpression ve = EXPRESSION_FACTORY.createValueExpression(EL_CONTEXT, valueExpression, Object.class);

   		@Override
         public String getExpressionString()
         {
            return valueExpression;
         }

         @Override
   		public Class getType(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
            return ve.getType(EL_CONTEXT);
   		}
   
   		@Override
   		public Object getValue(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   			return ve.getValue(EL_CONTEXT);
   		}
   
   		@Override
   		public boolean isReadOnly(FacesContext ctx) throws EvaluationException, PropertyNotFoundException {
   			return ve.isReadOnly(EL_CONTEXT);
   		}
   
   		@Override
   		public void setValue(FacesContext ctx, Object value) throws EvaluationException, PropertyNotFoundException {
            ve.setValue(EL_CONTEXT, value);
   		}
    	  
      };
   }

}
