package org.jboss.seam;

import java.util.Hashtable;
import java.util.Locale;

import org.hibernate.validator.ClassValidator;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.ResourceBundle;

/**
 * Base class of metamodels. For a class which
 * is neither an entity nor a Seam component,
 * the concrete type of the metamodel object
 * will be Model. For components or entities
 * it is a subclass of Model.
 * 
 * @author Gavin King
 *
 */
public class Model
{
   private Class<?> beanClass;
   private Hashtable<Locale, ClassValidator> validators = new Hashtable<Locale, ClassValidator>();

   public Model(Class<?> beanClass)
   {
      this.beanClass = beanClass;
   }
   
   public final Class<?> getBeanClass()
   {
      return beanClass;
   }

   public ClassValidator getValidator()
   {
      java.util.ResourceBundle bundle = Contexts.isApplicationContextActive() ? //yew, just for testing!
            ResourceBundle.instance() : null;
      Locale locale = bundle==null ?
            new Locale("DUMMY") : bundle.getLocale();
      ClassValidator validator = validators.get(locale);
      if (validator==null)
      {
         validator = bundle==null ?
               new ClassValidator(beanClass) :
               new ClassValidator(beanClass, bundle);
         validators.put(locale, validator);
      }
      return validator;
   }
   
   public static Model forClass(Class clazz)
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No application context active");
      }
      
      String componentName = Seam.getComponentName(clazz);
      if (componentName!=null)
      {
         return Component.forName(componentName);
      }
      else
      {
         String name = clazz.getName() + ".model";
         Model model = (Model) Contexts.getApplicationContext().get(name);
         if ( model==null )
         {
            model = clazz.isAnnotationPresent(javax.persistence.Entity.class) ? 
                     new Entity(clazz) : new Model(clazz);
            Contexts.getApplicationContext().set(name, model);
         }
         return model;
      }
   }

}
