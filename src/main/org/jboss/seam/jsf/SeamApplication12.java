package org.jboss.seam.jsf;

import java.lang.reflect.Method;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class SeamApplication12 extends SeamApplication11
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
  
   public SeamApplication12(Application application)
   {
      super(application);
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

}
