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
import org.jboss.seam.util.UnifiedELMethodBinding;
import org.jboss.seam.util.UnifiedELValueBinding;

/**
 * Factory for method and value bindings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.expressions")
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
               FacesContext context = FacesContext.getCurrentInstance();
               cachedValueBinding = context==null ? 
                     new UnifiedELValueBinding(expression) : 
                     context.getApplication().createValueBinding(expression);
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
         private transient javax.faces.el.MethodBinding cachedMethodBinding;

         public String getExpressionString()
         {
            return expression;
         }

         public Object invoke(Object... args)
         {
            return getFacesMethodBinding(args).invoke( FacesContext.getCurrentInstance(), args );
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
            
            if (cachedMethodBinding==null)
            {
               FacesContext context = FacesContext.getCurrentInstance();
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
   }
   
   public static Expressions instance()
   {
      return (Expressions) Component.getInstance(Expressions.class, ScopeType.APPLICATION);
   }
   
}
