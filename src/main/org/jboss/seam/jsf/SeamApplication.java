package org.jboss.seam.jsf;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

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

import org.jboss.seam.actionparam.ActionParamMethodBinding;
import org.jboss.seam.actionparam.ActionParamValueBinding;

public class SeamApplication extends Application
{

   private Application application;
   
   public Application getDelegate()
   {
      return application;
   }
   
   public SeamApplication(Application app)
   {
      this.application = app;
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
      return application.createConverter(converterId);
   }

   @Override
   public Converter createConverter(Class targetClass)
   {
      return application.createConverter(targetClass);
   }

   @Override
   public MethodBinding createMethodBinding(String ref, Class[] params)
         throws ReferenceSyntaxException
   {
      return new ActionParamMethodBinding(application, ref);
      //return app.createMethodBinding(ref, params);
   }

   @Override
   public Validator createValidator(String validatorId) throws FacesException
   {
      return application.createValidator(validatorId);
   }

   @Override
   public ValueBinding createValueBinding(String ref)
         throws ReferenceSyntaxException
   {
      return new ActionParamValueBinding(application, ref);
      //return app.createValueBinding(ref);
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
      return application.getMessageBundle();
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
