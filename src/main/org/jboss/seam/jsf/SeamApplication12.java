package org.jboss.seam.jsf;

import java.lang.reflect.Method;

import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

public class SeamApplication12 extends SeamApplication11
{
   
   private final Method getELResolverMethod;
   private final Method addELResolverMethod;
   private final Method getExpressionFactoryMethod;
   private final Method evaluateExpressionMethod;
  
   public SeamApplication12(Application application)
   {
      super(application);
      try
      {
         getELResolverMethod = application.getClass().getMethod("getELResolver");
         addELResolverMethod = application.getClass().getMethod("addELResolver", ELResolver.class);
         getExpressionFactoryMethod = application.getClass().getMethod("getExpressionFactory");
         evaluateExpressionMethod = application.getClass().getMethod("evaluateExpressionGet", FacesContext.class, String.class, Class.class);
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
   
   public ExpressionFactory getExpressionFactory() {
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
          Class expectedType) throws ELException {
      try
      {
         return evaluateExpressionMethod.invoke(application, context, expression, expectedType);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
