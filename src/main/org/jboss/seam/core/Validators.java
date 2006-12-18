package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Caches instances of Hibernate Validator ClassValidator
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.core.validators")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
@Install(precedence=BUILT_IN)
public class Validators
{

   private Map<Class, ClassValidator> classValidators = Collections.synchronizedMap( new HashMap<Class, ClassValidator>() ); 
   
   public ClassValidator getValidator(Class modelClass, String name)
   {
      ClassValidator result = classValidators.get(modelClass);
      if (result==null)
      {
         result = createValidator(modelClass, name);
         classValidators.put(modelClass, result);
      }
      return result;
   }
   
   public ClassValidator createValidator(Class modelClass, String name)
   {
      Component component = name==null ? null : Component.forName(name);
      if (component==null)
      {
         java.util.ResourceBundle bundle = ResourceBundle.instance();
         return bundle==null ? 
               new ClassValidator(modelClass) : 
               new ClassValidator(modelClass, bundle);
      }
      else
      {
         return component.getValidator();
      }
   }

   public InvalidValue[] validate(FacesContext context, String propertyExpression, Object value)
   {
      int dot = propertyExpression.lastIndexOf('.');
      int bracket = propertyExpression.lastIndexOf('[');
      if (dot<=0 && bracket<=0) 
      {
         return new InvalidValue[0];
      }
      String componentName;
      String propertyName;
      if (dot>bracket)
      {
         componentName = propertyExpression.substring(2, dot);
         propertyName = propertyExpression.substring( dot+1, propertyExpression.length()-1 );
      }
      else
      {
         componentName = propertyExpression.substring(2, bracket);
         propertyName = propertyExpression.substring( bracket+1, propertyExpression.length()-2 );
      }
   
      String modelExpression = propertyExpression.substring(0, dot) + '}';
      Object model = context.getApplication().createValueBinding(modelExpression).getValue(context);
      ClassValidator validator = getValidator( model.getClass(), componentName );    
      return validator.getPotentialInvalidValues(propertyName, value);
   }

   public static Validators instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (Validators) Component.getInstance(Validators.class, ScopeType.APPLICATION);
   }

}
