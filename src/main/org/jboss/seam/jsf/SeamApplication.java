package org.jboss.seam.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;

@SuppressWarnings("deprecation")
public class SeamApplication extends Application
{  
   
   protected Application application;
   
   public SeamApplication(Application application)
   {
      this.application = application;
   }
   
   public Application getDelegate()
   {
      return application;
   }
   
   @Override
   public ELResolver getELResolver() 
   {
      return application.getELResolver();
   }

   @Override
   public void addComponent(String componentType, String componentClass)
   {
      application.addComponent(componentType, componentClass);
   }

   @Override
   public void addConverter(String converterId, String converterClass)
   {
      application.addConverter(converterId, converterClass);
   }

   @Override
   public void addConverter(Class targetClass, String converterClass)
   {
      application.addConverter(targetClass, converterClass);
   }

   @Override
   public void addValidator(String validatorId, String validatorClass)
   {
      application.addValidator(validatorId, validatorClass);
   }

   @Override
   public UIComponent createComponent(String componentType)
         throws FacesException
   {
      return application.createComponent(componentType);
   }

   @Override
   public UIComponent createComponent(ValueBinding componentBinding,
         FacesContext context, String componentType) throws FacesException
   {
      return application.createComponent(componentBinding, context, componentType);
   }

   @Override
   public Converter createConverter(String converterId)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         String name = Init.instance().getConverters().get(converterId);
         if (name!=null)
         {
            return (Converter) Component.getInstance(name);
         }
      }
      return application.createConverter(converterId);
   }

   @Override
   public Converter createConverter(Class targetClass)
   {
      if ( Contexts.isApplicationContextActive() )
      {
         String name = Init.instance().getConvertersByClass().get(targetClass);
         if (name!=null)
         {
            return (Converter) Component.getInstance(name);
         }
      }
      return application.createConverter(targetClass);
   }

   @Override
   public MethodBinding createMethodBinding(String expression, Class[] params)
         throws ReferenceSyntaxException
   {
      //TODO: if ( paramTypes.length==1 && FacesEvent.class.isAssignableFrom( paramTypes[0] ) )
      //      return new OptionalParamMethodBinding(expression,params)       
      return application.createMethodBinding(expression, params);

   }

   @Override
   public Validator createValidator(String validatorId) throws FacesException
   {
      if ( Contexts.isApplicationContextActive() )
      {
         String name = Init.instance().getValidators().get(validatorId);
         if (name!=null)
         {
            return (Validator) Component.getInstance(name);
         }
      }
      return application.createValidator(validatorId);
   }

   @Override
   public ValueBinding createValueBinding(String ref)
         throws ReferenceSyntaxException
   {
      return application.createValueBinding(ref);
   }

   @Override
   public ActionListener getActionListener()
   {
      return application.getActionListener();
   }

   @Override
   public Iterator getComponentTypes()
   {
      return application.getComponentTypes();
   }

   @Override
   public Iterator getConverterIds()
   {
      return application.getConverterIds();
   }

   @Override
   public Iterator getConverterTypes()
   {
      return application.getComponentTypes();
   }

   @Override
   public Locale getDefaultLocale()
   {
      return application.getDefaultLocale();
   }

   @Override
   public String getDefaultRenderKitId()
   {
      return application.getDefaultRenderKitId();
   }

   @Override
   public String getMessageBundle()
   {
      if (application.getMessageBundle() != null)
      {
         return application.getMessageBundle();
      }
      else
      {
         return "org.jboss.seam.jsf.SeamApplicationMessageBundle";
      }
   }

   @Override
   public NavigationHandler getNavigationHandler()
   {
      return application.getNavigationHandler();
   }

   @Override
   public PropertyResolver getPropertyResolver()
   {
      return application.getPropertyResolver();
   }

   @Override
   public StateManager getStateManager()
   {
      return application.getStateManager();
   }

   @Override
   public Iterator getSupportedLocales()
   {
      return application.getSupportedLocales();
   }

   @Override
   public Iterator getValidatorIds()
   {
      return application.getValidatorIds();
   }

   @Override
   public VariableResolver getVariableResolver()
   {
      return application.getVariableResolver();
   }

   @Override
   public ViewHandler getViewHandler()
   {
      return application.getViewHandler();
   }

   @Override
   public void setActionListener(ActionListener listener)
   {
      application.setActionListener(listener);
   }

   @Override
   public void setDefaultLocale(Locale locale)
   {
      application.setDefaultLocale(locale);
   }

   @Override
   public void setDefaultRenderKitId(String renderKitId)
   {
      application.setDefaultRenderKitId(renderKitId);
   }

   @Override
   public void setMessageBundle(String bundle)
   {
      application.setMessageBundle(bundle);
   }

   @Override
   public void setNavigationHandler(NavigationHandler handler)
   {
      application.setNavigationHandler(handler);
   }

   @Override
   public void setPropertyResolver(PropertyResolver resolver)
   {
      application.setPropertyResolver(resolver);
   }

   @Override
   public void setStateManager(StateManager manager)
   {
      application.setStateManager(manager);
   }

   @Override
   public void setSupportedLocales(Collection locales)
   {
      application.setSupportedLocales(locales);
   }

   @Override
   public void setVariableResolver(VariableResolver resolver)
   {
      application.setVariableResolver(resolver);
   }

   @Override
   public void setViewHandler(ViewHandler handler)
   {
      application.setViewHandler(handler);
   }

   @Override
   public void addELContextListener(ELContextListener arg0)
   {
      application.addELContextListener(arg0);
   }

   @Override
   public void addELResolver(ELResolver arg0)
   {
      application.addELResolver(arg0);
   }

   @Override
   public UIComponent createComponent(ValueExpression arg0, FacesContext arg1, String arg2) throws FacesException
   {
      return application.createComponent(arg0, arg1, arg2);
   }

   @Override
   public Object evaluateExpressionGet(FacesContext arg0, String arg1, Class arg2) throws ELException
   {
      return application.evaluateExpressionGet(arg0, arg1, arg2);
   }

   @Override
   public ELContextListener[] getELContextListeners()
   {
      return application.getELContextListeners();
   }

   @Override
   public ExpressionFactory getExpressionFactory()
   {
      return application.getExpressionFactory();
   }

   @Override
   public ResourceBundle getResourceBundle(FacesContext arg0, String arg1)
   {
      return application.getResourceBundle(arg0, arg1);
   }

   @Override
   public void removeELContextListener(ELContextListener arg0)
   {
      application.removeELContextListener(arg0);
   }

   @Override
   public String toString()
   {
      return application.toString();
   }
   
}
