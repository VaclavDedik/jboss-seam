//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.util.EL.EL_CONTEXT;
import static org.jboss.seam.util.EL.EXPRESSION_FACTORY;

import java.io.Serializable;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.Model;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.UnifiedELMethodBinding;

/**
 * Factory for method and value bindings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Install(precedence=BUILT_IN)
@Name("org.jboss.seam.core.expressions")
public class Expressions
    implements Serializable
{
   
   public ValueBinding createValueBinding(final String expression)
   {
      
      return new ValueBinding() 
      {
         private static final long serialVersionUID = -8655967672318993009L;
         
         private transient javax.faces.el.ValueBinding cachedValueBinding;
         private transient ValueExpression cachedValueExpression;
         
         public String getExpressionString()
         {
            return expression;
         }

         public Class getType()
         {
            if ( isFacesContext() )
            {
               return getFacesValueBinding().getType( FacesContext.getCurrentInstance() );
            }
            else
            {
               return getValueExpression().getType(EL_CONTEXT);
            }
         }

         public Object getValue()
         {
            if ( isFacesContext() )
            {
               return getFacesValueBinding().getValue( FacesContext.getCurrentInstance() );
            }
            else
            {
               return getValueExpression().getValue(EL_CONTEXT);
            }
         }

         public boolean isReadOnly()
         {
            if ( isFacesContext() )
            {
               return getFacesValueBinding().isReadOnly( FacesContext.getCurrentInstance() );
            }
            else
            {
               return getValueExpression().isReadOnly(EL_CONTEXT);
            }
         }

         public void setValue(Object value)
         {
            if ( isFacesContext() )
            {
               getFacesValueBinding().setValue( FacesContext.getCurrentInstance(), value );
            }
            else
            {
               getValueExpression().setValue(EL_CONTEXT, value);
            }
         }
         
         boolean isFacesContext()
         {
            return FacesContext.getCurrentInstance()!=null;
         }
         
         ValueExpression getValueExpression()
         {
            if (cachedValueExpression==null)
            {
               cachedValueExpression = EXPRESSION_FACTORY.createValueExpression(EL_CONTEXT, expression, Object.class);
            }
            return cachedValueExpression;
         }

         javax.faces.el.ValueBinding getFacesValueBinding()
         {
            if (cachedValueBinding==null)
            {
               cachedValueBinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(expression);
            }
            return cachedValueBinding;
         }
         
         @Override
         public String toString()
         {
            return getExpressionString();
         }
      
      };
   }
   
   public MethodBinding createMethodBinding(final String expression)
   {
      return new MethodBinding() 
      {
         private static final long serialVersionUID = 7314202661786534543L;
         
         private transient javax.faces.el.MethodBinding cachedMethodBinding;

         public String getExpressionString()
         {
            return expression;
         }

         public Object invoke(Object... args)
         {
            return getFacesMethodBinding(args).invoke( FacesContext.getCurrentInstance(), args );
         }
         
         public Object invoke(Class[] argTypes, Object... args)
         {
            return getFacesMethodBinding(argTypes, args).invoke(FacesContext.getCurrentInstance(), args);
         }

         private javax.faces.el.MethodBinding getFacesMethodBinding(Object... args)
         {
            Class[] types = new Class[args.length];
            for (int i=0; i<args.length;i++)
            {
               if (args[i]==null)
               {
                  throw new IllegalArgumentException("Null parameter");
               }
               types[i] = args[i].getClass();
            }
            return getFacesMethodBinding(types, args);
         }
         
         private javax.faces.el.MethodBinding getFacesMethodBinding(Class[] types, Object... args)
         {
            FacesContext context = FacesContext.getCurrentInstance();
            if (cachedMethodBinding==null || (context == null && !(cachedMethodBinding instanceof UnifiedELMethodBinding)))
            {     
               cachedMethodBinding = context==null ? 
                     new UnifiedELMethodBinding(expression, types) : 
                     context.getApplication().createMethodBinding(expression, types);
            }
            return cachedMethodBinding;            
         }
         
         @Override
         public String toString()
         {
            return getExpressionString();
         }
      
      };
      
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
         componentName = propertyExpression.substring(2, dot);
         propertyName = propertyExpression.substring( dot+1, propertyExpression.length()-1 );
         modelExpression = propertyExpression.substring(0, dot) + '}';
      }
      else
      {
         componentName = propertyExpression.substring(2, bracket);
         propertyName = propertyExpression.substring( bracket+1, propertyExpression.length()-2 );
         modelExpression = propertyExpression.substring(0, bracket) + '}';
      }
      
      Object modelInstance = createValueBinding(modelExpression).getValue(); //TODO: cache the ValueBinding object!
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

   public static interface ValueBinding<T> extends Serializable
   {
       public String getExpressionString();
       public Class<T> getType();
       public T getValue();
       public boolean isReadOnly();
       public void setValue(T value);
   }
   
   public static interface MethodBinding<T> extends Serializable
   {
      public String getExpressionString();
      public T invoke(Object... args);
      public T invoke(Class[] argTypes, Object... args);
   }
   
   public static Expressions instance()
   {
      return (Expressions) Component.getInstance(Expressions.class, ScopeType.APPLICATION);
   }
   
}
