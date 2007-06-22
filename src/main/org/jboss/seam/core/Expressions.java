//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.el.EL.EL_CONTEXT;

import java.io.Serializable;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.Model;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.el.SeamExpressionFactory;

/**
 * Factory for EL method and value expressions.
 * 
 * This default implementation uses JBoss EL.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.expressions")
public class Expressions implements Serializable
{
   
   /**
    * Get the JBoss EL ExpressionFactory
    */
   public ExpressionFactory getExpressionFactory()
   {
      return SeamExpressionFactory.INSTANCE;
   }
   
   /**
    * Get an appropriate ELContext. If there is an active JSF request,
    * use JSF's ELContext. Otherwise, use one that we created.
    */
   public ELContext getELContext()
   {
      return EL_CONTEXT;
   }

   /**
    * Create a value expression.
    * 
    * @param expression a JBoss EL value expression
    */
   public ValueExpression<Object> createValueExpression(String expression)
   {
      return createValueExpression(expression, Object.class);
   }
   
   /**
    * Create a method expression.
    * 
    * @param expression a JBoss EL method expression
    */
   public MethodExpression<Object> createMethodExpression(String expression)
   {
      return createMethodExpression(expression, Object.class);
   }
   
   /**
    * Create a value expression.
    * 
    * @param expression a JBoss EL value expression
    * @param type the type of the value 
    */
   public <T> ValueExpression<T> createValueExpression(final String expression, final Class<T> type)
   {
      
      return new ValueExpression<T>()
      {
         private javax.el.ValueExpression facesValueExpression;
         private javax.el.ValueExpression seamValueExpression;
         
         private javax.el.ValueExpression getExpression()
         {
            if ( isFacesContextActive() )
            {
               if (seamValueExpression==null)
               {
                  seamValueExpression = createExpression();
               }
               return seamValueExpression;
            }
            else
            {
               if (facesValueExpression==null)
               {
                  facesValueExpression = createExpression();
               }
               return facesValueExpression;
            }
         }
         
         private javax.el.ValueExpression createExpression()
         {
            return getExpressionFactory().createValueExpression( getELContext(), expression, type );
         }
         
         public T getValue()
         {
            return (T) getExpression().getValue( getELContext() );
         }
         
         public void setValue(T value)
         {
            getExpression().setValue( getELContext(), value );
         }
         
         public String getExpressionString()
         {
            return expression;
         }
         
         public Class<T> getType()
         {
            return (Class<T>) getExpression().getType( getELContext() );
         }
         
      };
   }
   
   /**
    * Create a method expression.
    * 
    * @param expression a JBoss EL method expression
    * @param type the method return type
    * @param argTypes the method parameter types
    */
   public <T> MethodExpression<T> createMethodExpression(final String expression, final Class<T> type, final Class... argTypes)
   {
      return new MethodExpression<T>()
      {
         private javax.el.MethodExpression facesMethodExpression;
         private javax.el.MethodExpression seamMethodExpression;
         
         private javax.el.MethodExpression getExpression()
         {
            if ( isFacesContextActive() )
            {
               if (seamMethodExpression==null)
               {
                  seamMethodExpression = createExpression();
               }
               return seamMethodExpression;
            }
            else
            {
               if (facesMethodExpression==null)
               {
                  facesMethodExpression = createExpression();
               }
               return facesMethodExpression;
            }
         }
         
         private javax.el.MethodExpression createExpression()
         {
            return getExpressionFactory().createMethodExpression( getELContext(), expression, type, argTypes );
         }
         
         public T invoke(Object... args)
         {
            return (T) getExpression().invoke( getELContext(), args );
         }
         
         public String getExpressionString()
         {
            return expression;
         }
         
      };
   }
   
   /**
    * A value expression, an EL expression that evaluates to
    * an attribute getter or get/set pair.
    * 
    * @author Gavin King
    *
    * @param <T> the type of the value
    */
   public static interface ValueExpression<T> extends Serializable
   {
      public T getValue();
      public void setValue(T value);
      public String getExpressionString();
      public Class<T> getType();
   }
   
   /**
    * A method expression, an EL expression that evaluates to
    * a method.
    * 
    * @author Gavin King
    *
    * @param <T> the method return type
    */
   public static interface MethodExpression<T> extends Serializable
   {
      public T invoke(Object... args);
      public String getExpressionString();
   }
   
   protected boolean isFacesContextActive()
   {
      return false;
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
   public InvalidValue[] getInvalidValues(String propertyExpression, Object value)
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
