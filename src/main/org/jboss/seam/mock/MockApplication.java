package org.jboss.seam.mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.el.ExpressionFactory;
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
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import org.jboss.seam.util.EL;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.UnifiedELMethodBinding;
import org.jboss.seam.util.UnifiedELValueBinding;

public class MockApplication extends Application
{

   @Override
   public ActionListener getActionListener()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setActionListener(ActionListener listener)
   {
      throw new UnsupportedOperationException();
   }

   private Locale defaultLocale = Locale.ENGLISH;

   @Override
   public Locale getDefaultLocale()
   {
      return defaultLocale;
   }

   @Override
   public void setDefaultLocale(Locale locale)
   {
      defaultLocale = locale;
   }

   @Override
   public String getDefaultRenderKitId()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setDefaultRenderKitId(String rk)
   {
      throw new UnsupportedOperationException();
   }

   private String msgBundleName;

   @Override
   public String getMessageBundle()
   {
      return msgBundleName;
   }

   @Override
   public void setMessageBundle(String bundleName)
   {
      this.msgBundleName = bundleName;
   }

   private NavigationHandler navigationHandler = new MockNavigationHandler();

   @Override
   public NavigationHandler getNavigationHandler()
   {
      return navigationHandler;
   }

   @Override
   public void setNavigationHandler(NavigationHandler navigationHandler)
   {
      this.navigationHandler = navigationHandler;
   }

   @Override
   public PropertyResolver getPropertyResolver()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setPropertyResolver(PropertyResolver pr)
   {
      throw new UnsupportedOperationException();
   }

   private VariableResolver variableResolver = null; // TODO: big big todo!!!!!!!!!!

   @Override
   public VariableResolver getVariableResolver()
   {
      return variableResolver;
   }

   @Override
   public void setVariableResolver(VariableResolver variableResolver)
   {
      this.variableResolver = variableResolver;
   }

   private ViewHandler viewHandler = new MockViewHandler();

   @Override
   public ViewHandler getViewHandler()
   {
      return viewHandler;
   }

   @Override
   public void setViewHandler(ViewHandler viewHandler)
   {
      this.viewHandler = viewHandler;
   }

   private StateManager stateManager = new MockStateManager();

   @Override
   public StateManager getStateManager()
   {
      return stateManager;
   }

   @Override
   public void setStateManager(StateManager stateManager)
   {
      this.stateManager = stateManager;
   }

   @Override
   public void addComponent(String name, String x)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIComponent createComponent(String name) throws FacesException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public UIComponent createComponent(ValueBinding vb, FacesContext fc, String x)
            throws FacesException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator getComponentTypes()
   {
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

   private final Map<String, Converter> convertersById = new HashMap<String, Converter>();
   {
      convertersById.put(IntegerConverter.CONVERTER_ID, new IntegerConverter());
      convertersById.put(LongConverter.CONVERTER_ID, new LongConverter());
      convertersById.put(FloatConverter.CONVERTER_ID, new FloatConverter());
      convertersById.put(DoubleConverter.CONVERTER_ID, new DoubleConverter());
      convertersById.put(BooleanConverter.CONVERTER_ID, new BooleanConverter());
      convertersById.put(ShortConverter.CONVERTER_ID, new ShortConverter());
      convertersById.put(ByteConverter.CONVERTER_ID, new ByteConverter());
      convertersById.put(CharacterConverter.CONVERTER_ID, new CharacterConverter());
      convertersById.put(BigDecimalConverter.CONVERTER_ID, new BigDecimalConverter());
      convertersById.put(BigIntegerConverter.CONVERTER_ID, new BigIntegerConverter());
   }

   @Override
   public void addConverter(String id, String converterClass)
   {
      convertersById.put(id, instantiateConverter(converterClass));
   }

   @Override
   public void addConverter(Class type, String converterClass)
   {
      converters.put(type, instantiateConverter(converterClass));
   }

   private Converter instantiateConverter(String converterClass)
   {
      try
      {
         return (Converter) Reflections.classForName(converterClass).newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Converter createConverter(String id)
   {
      return convertersById.get(id);
   }

   @Override
   public Converter createConverter(Class clazz)
   {
      return converters.get(clazz);
   }

   @Override
   public Iterator getConverterIds()
   {
      return convertersById.keySet().iterator();
   }

   @Override
   public Iterator getConverterTypes()
   {
      return converters.keySet().iterator();
   }

   @Override
   public MethodBinding createMethodBinding(final String methodExpression, final Class[] args)
            throws ReferenceSyntaxException
   {
      Class[] c = args;
      if (c == null)
      {
         // Mismatch between JSF and Unified EL
         c = new Class[0];
      }  
      return new UnifiedELMethodBinding(methodExpression, c);
   }

   @Override
   public Iterator getSupportedLocales()
   {
      return Collections.singleton(defaultLocale).iterator();
   }

   @Override
   public void setSupportedLocales(Collection locales)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addValidator(String id, String validator)
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Validator createValidator(String id) throws FacesException
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterator getValidatorIds()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public ValueBinding createValueBinding(final String valueExpression)
            throws ReferenceSyntaxException
   {
      return new UnifiedELValueBinding(valueExpression);
   }
   
   public ExpressionFactory getExpressionFactory()
   {
      return EL.EXPRESSION_FACTORY;
   }
   
}
