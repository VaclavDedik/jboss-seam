package org.jboss.seam.jsf;

import java.lang.reflect.Method;
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
   
   private final Method getELResolverMethod;
   private final Method addELResolverMethod;
   private final Method getExpressionFactoryMethod;
   private final Method evaluateExpressionMethod;
   private final Method getResourceBundleMethod;
   private final Method createComponentMethod;
   private final Method getELContextListenersMethod;
   private final Method addELContextListenerMethod;
   private final Method removeELContextListenerMethod;
  
   public SeamApplication(Application application)
   {
      this.application = application;
         
      try
      {
         getELResolverMethod = application.getClass().getMethod("getELResolver");
         addELResolverMethod = application.getClass().getMethod("addELResolver", ELResolver.class);
         getExpressionFactoryMethod = application.getClass().getMethod("getExpressionFactory");
         evaluateExpressionMethod = application.getClass().getMethod("evaluateExpressionGet", FacesContext.class, String.class, Class.class);
         getResourceBundleMethod = application.getClass().getMethod("getResourceBundle", FacesContext.class, String.class);
         createComponentMethod = application.getClass().getMethod("createComponent", ValueExpression.class, FacesContext.class, String.class);
         getELContextListenersMethod = application.getClass().getMethod("getELContextListeners");
         addELContextListenerMethod = application.getClass().getMethod("addELContextListener", ELContextListener.class);
         removeELContextListenerMethod = application.getClass().getMethod("removeELContextListener", ELContextListener.class);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   

   @Override
   public ELResolver getELResolver() 
   {
      try
      {
         return (ELResolver) getELResolverMethod.invoke(application);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public void addELResolver(ELResolver resolver) 
   {
      try
      {
         addELResolverMethod.invoke(application, resolver);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public ExpressionFactory getExpressionFactory() 
   {
      try
      {
         return (ExpressionFactory) getExpressionFactoryMethod.invoke(application);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public Object evaluateExpressionGet(FacesContext context, String expression, 
          Class expectedType) throws ELException 
          {
      try
      {
         return evaluateExpressionMethod.invoke(application, context, expression, expectedType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public ResourceBundle getResourceBundle(FacesContext ctx, String name) 
   {
      try
      {
         return (ResourceBundle) getResourceBundleMethod.invoke(application, ctx, name);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public UIComponent createComponent(ValueExpression componentExpression,
         FacesContext context, String componentType) throws FacesException
   {
      try
      {
         return (UIComponent) createComponentMethod.invoke(application, componentExpression, context, componentType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public void addELContextListener(ELContextListener listener)
   {
      try
      {
         addELContextListenerMethod.invoke(application, listener);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public void removeELContextListener(ELContextListener listener)
   {
      try
      {
         removeELContextListenerMethod.invoke(application, listener);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   public ELContextListener[] getELContextListeners()
   {
      try
      {
         return (ELContextListener[]) getELContextListenersMethod.invoke(application);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   protected Application application;
   
   public Application getDelegate()
   {
      return application;
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
   
}
