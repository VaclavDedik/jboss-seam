//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

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
   
   public ValueBinding createValueBinding(String expression)
   {
      final javax.faces.el.ValueBinding vb = FacesContext.getCurrentInstance()
            .getApplication().createValueBinding(expression);
      
      return new ValueBinding() {

         public String getExpressionString()
         {
            return vb.getExpressionString();
         }

         public Class getType()
         {
            return vb.getType( FacesContext.getCurrentInstance() );
         }

         public Object getValue()
         {
            return vb.getValue( FacesContext.getCurrentInstance() );
         }

         public boolean isReadOnly()
         {
            return vb.isReadOnly( FacesContext.getCurrentInstance() );
         }

         public void setValue(Object value)
         {
            vb.setValue( FacesContext.getCurrentInstance(), value );
         }
         
      };
   }
   
   public MethodBinding createMethodBinding(String expression)
   {
      final javax.faces.el.MethodBinding mb = FacesContext.getCurrentInstance()
            .getApplication().createMethodBinding(expression, null);

      return new MethodBinding() {

         public String getExpressionString()
         {
            return mb.getExpressionString();
         }

         public Class getType()
         {
            return mb.getType( FacesContext.getCurrentInstance() );
         }

         public Object invoke(Object[] aobj)
         {
            return mb.invoke( FacesContext.getCurrentInstance(), aobj );
         }
      
      };
      
   }

   public static interface ValueBinding
   {
       public String getExpressionString();

       public Class getType();

       public Object getValue();

       public boolean isReadOnly();

       public void setValue(Object value);
   }
   
   public static interface MethodBinding {
      public String getExpressionString();
      
      public Class getType();

      public Object invoke( Object aobj[] );
   }
   
   public static Expressions instance()
   {
      return (Expressions) Component.getInstance(Expressions.class);
   }
   
}
