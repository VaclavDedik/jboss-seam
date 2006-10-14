//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

/**
 * Factory for method and value bindings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("expressions")
@Startup
public class Expressions
{
   
   public ValueBinding createValueBinding(final String expression)
   {
      
      return new ValueBinding() 
      {
         
         private transient javax.faces.el.ValueBinding cachedValueBinding;
         
         public String getExpressionString()
         {
            return expression;
         }

         public Class getType()
         {
            return getFacesValueBinding().getType( FacesContext.getCurrentInstance() );
         }

         public Object getValue()
         {
            return getFacesValueBinding().getValue( FacesContext.getCurrentInstance() );
         }

         public boolean isReadOnly()
         {
            return getFacesValueBinding().isReadOnly( FacesContext.getCurrentInstance() );
         }

         public void setValue(Object value)
         {
            getFacesValueBinding().setValue( FacesContext.getCurrentInstance(), value );
         }

         javax.faces.el.ValueBinding getFacesValueBinding()
         {
            if (cachedValueBinding==null)
            {
               cachedValueBinding = FacesContext.getCurrentInstance().getApplication().createValueBinding(expression);
            }
            return cachedValueBinding;
         }
         
      };
   }
   
   public MethodBinding createMethodBinding(final String expression)
   {
      return new MethodBinding() 
      {
         
         private transient javax.faces.el.MethodBinding cachedMethodBinding;

         public String getExpressionString()
         {
            return getFacesMethodBinding().getExpressionString();
         }

         public Class getType()
         {
            return getFacesMethodBinding().getType( FacesContext.getCurrentInstance() );
         }

         public Object invoke(Object... args)
         {
            return getFacesMethodBinding().invoke( FacesContext.getCurrentInstance(), args );
         }

         javax.faces.el.MethodBinding getFacesMethodBinding()
         {
            if (cachedMethodBinding==null)
            {
               cachedMethodBinding = FacesContext.getCurrentInstance().getApplication().createMethodBinding(expression, null);
            }
            return cachedMethodBinding;
         }
      
      };
      
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
      public Class<T> getType();
      public T invoke(Object... args);
   }
   
   public static Expressions instance()
   {
      return (Expressions) Component.getInstance(Expressions.class);
   }
   
}
