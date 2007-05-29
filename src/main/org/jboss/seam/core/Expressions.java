//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.util.EL.EL_CONTEXT;
import static org.jboss.seam.util.EL.EXPRESSION_FACTORY;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.el.lang.EvaluationContext;
import org.jboss.seam.Component;
import org.jboss.seam.Model;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.jsf.SeamELFunctionMapper;

/**
 * Factory for method and value bindings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.expressions")
public class Expressions implements Serializable
{
   
   public ExpressionFactory getExpressionFactory()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      return facesContext==null ? 
            EXPRESSION_FACTORY : 
            facesContext.getApplication().getExpressionFactory();
   }
   
   public ELContext getELContext()
   {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      if ( facesContext==null )
      {
         return EL_CONTEXT;
      }
      else
      {
         ELContext context = facesContext.getELContext();
         return new EvaluationContext( context, new SeamELFunctionMapper( context.getFunctionMapper() ), context.getVariableMapper() );
      }
   }
   
   public ValueExpression<Object> createValueExpression(String expression)
   {
      return createValueExpression(expression, Object.class);
   }
   
   public MethodExpression<Object> createMethodExpression(String expression)
   {
      return createMethodExpression(expression, Object.class);
   }
   
   public <T> ValueExpression<T> createValueExpression(final String expression, final Class<T> type)
   {
      //TODO: cache the VEs
      return new ValueExpression<T>()
      {
         private javax.el.ValueExpression createExpression()
         {
            return getExpressionFactory().createValueExpression( getELContext(), expression, type );
         }
         public T getValue()
         {
            return (T) createExpression().getValue( getELContext() );
         }
         public void setValue(T value)
         {
            createExpression().setValue( getELContext(), value );
         }
         public String getExpressionString()
         {
            return expression;
         }
         public Class<T> getType()
         {
            return (Class<T>) createExpression().getType( getELContext() );
         }
      };
   }
   
   public <T> MethodExpression<T> createMethodExpression(final String expression, final Class<T> type, final Class... argTypes)
   {
      //TODO: cache the MEs
      return new MethodExpression<T>()
      {
         private javax.el.MethodExpression createExpression()
         {
            return getExpressionFactory().createMethodExpression( getELContext(), expression, type, argTypes );
         }
         public T invoke(Object... args)
         {
            return (T) createExpression().invoke( getELContext(), args );
         }
         public String getExpressionString()
         {
            return expression;
         }
      };
   }
   
   public static interface ValueExpression<T> extends Serializable
   {
      public T getValue();
      public void setValue(T value);
      public String getExpressionString();
      public Class<T> getType();
   }
   
   public static interface MethodExpression<T> extends Serializable
   {
      public T invoke(Object... args);
      public String getExpressionString();
   }
   

   /**
    * Validate that a value can be assigned to the property
    * identified by a value expression.
    * 
    * @param propertyExpression a value expression
    * @param value the value that is to be assigned
    * 
    * @return the validation failures, as InvalidValues
    */
   public InvalidValue[] validate(String propertyExpression, Object value)
   {
      if (propertyExpression == null)
      {
         return new InvalidValue[0];
      }
      int dot = propertyExpression.lastIndexOf('.');
      int bracket = propertyExpression.lastIndexOf('[');
      if (dot<=0 && bracket<=0) 
      {
         return new InvalidValue[0];
      }
      String componentName;
      String propertyName;
      String modelExpression;
      if (dot>bracket)
      {
         componentName = propertyExpression.substring(2, dot).trim();
         propertyName = propertyExpression.substring( dot+1, propertyExpression.length()-1 ).trim();
         modelExpression = propertyExpression.substring(0, dot).trim() + '}';
      }
      else
      {
         componentName = propertyExpression.substring(2, bracket).trim();
         propertyName = propertyExpression.substring( bracket+1, propertyExpression.length()-2 ).trim();
         if ( propertyName.startsWith("'") && propertyName.endsWith("'") )
         {
            propertyName = propertyName.substring( 1, propertyName.length()-1 );
            //TODO: handle meaningless property names here!
         }
         else
         {
            return new InvalidValue[0];
         }
         modelExpression = propertyExpression.substring(0, bracket).trim() + '}';
      }
      
      Object modelInstance = getExpressionFactory().createValueExpression( getELContext(), modelExpression, Object.class)
               .getValue( getELContext() ); //TODO: cache the ValueExpression object!
      return getValidator(modelInstance, componentName).getPotentialInvalidValues(propertyName, value);
   }
   
   /**
    * Gets the validator from the Component object (if this is a Seam
    * component, we need to use the validator for the bean class, not
    * the proxy class) or from a Model object (if it is not a Seam
    * component, there isn't any proxy).
    * 
    * @param instance the object to be validated
    * @param componentName the name of the context variable, which might be a component name
    * @return a ClassValidator object
    */
   private static ClassValidator getValidator(Object instance, String componentName)
   {
      if (instance==null || componentName==null )
      {
         throw new IllegalArgumentException();
      }
      Component component = Component.forName(componentName);
      return ( component==null ? Model.forClass( instance.getClass() ) : component ).getValidator();
   }
   
   public static Expressions instance()
   {
      return (Expressions) Component.getInstance(Expressions.class, ScopeType.APPLICATION);
   }
   
}
